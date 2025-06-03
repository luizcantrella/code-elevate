## Tecnologias

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
- [Spring Data Redis](https://spring.io/projects/spring-data-redis)
- [Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb)
- [Spring Security](https://spring.io/projects/spring-security)
- [Postgres](https://www.postgresql.org)
- [MongoDB](https://www.mongodb.com/)
- [Docker Compose](https://docs.docker.com/compose/)
- [KeyCloak](https://www.keycloak.org/)

## Como executar o projeto

- Inicialize o ambiente executando o comando para subir as imagens no docker-compose:
```
docker-compose up --build -d
```

- Execute o comando para verificar o log do serviço **processor**:
```
docker logs processor
```

- Saída esperada no final do log do processor:
```
CSV file processed successfully.
```

- Carregue a coleção e variável de ambiente no [Postman](https://www.postman.com/) ou algum outro cliente de API de sua preferência:
```
./postman
```

- A coleção contem as chamadas de listar todos os livros, buscar por ID, lista os livros vistos recentemente pelo Id do usuário,
lista livros por genero, listar livros pelo autor e uma chamada para obter o token de acesso, pois todos os endpoins estão protegidos,
sendo assim só podem ser acessados com um token de acesso válido, caso contrário resultará num retorno 401, not authorised.


## Abordagem da solução 

### Arquitetura de solução e arquitetura técnica

- Mediante o problema proposto, o uso do mongoDB se faz melhor que um Postgres dado sua escalabilidade horizontal,
o mongoDB também se faz uma boa escolha tendo em vista que a solução utilizada para carregar os livros previamente
foi carregar os dados do keagle apenas de livros, com isso o sistema não apresenta necessidade de normalização,
tornando um banco de dados orientados a documento como o mongoDB uma boa escolha. Outro ponto é que o problema
proposto não apresenta necessidades de insert e update, com isso um banco dedos que prioriza a disponibilidade (AP do CAP)
é mais um ponto que favorece a escolha do mongoDB para este caso;
- E outro ponto que impacta positivamente melhorando a disponibilidade e escalabilidade de um sistema como este é o uso de cache,
como o sistema apenas apresentas leituras, deixar os dados mais buscados no cache alivia a pressão no banco de dados e diminui
o tempo de resposta para o usuário, deixando a experiência de uso da aplicação mais agradável, e com o servidor retornando mais
rápido as requisições por retornar dados do cache, com isso ele consegue le dar com mais requisições com os recursos alocados;
- Na parte de design foi utilizado a arquitetura hexagonal para melhor estruturar as camadas e facilitar o processo de mudança entre
tecnologias caso seja necessário, da forma que foi construido a troca para um outro banco de dados ou cache se torna mais fácil e
sem impactar a regra de negócio que por ventura estejam nos casos de uso ou no domínio;

### Explicação sobre o Case Desenvolvido

- O desafio proposto foca mais na busca de dados, com isso para a primeira etapa que era de obter os dados fakes ou de fontes externas
foi selecionado a base do keagle, para que os dados de livros fosse carregados na aplicação tem um processor que executar uma leitura
do arquivo CSV e persiste os dados no mongoDB, com isso deixando a base de dados para que a API com os endpoints que disponibiliza
a busca tenha dados disponíveis quando estiver de pé.
- Como a busca de todos os dados trás muitos registros (a base possui mais de 7 mil livros), a listagem de todos possui paginação,
onde é possível informar a página e a quantidade de registros por página. Aqui foi implementado o cache da página, onde para que seja
possível essa inserção e busca no cache é necessário um tratamento em especial, já que o PageImpl opera com genérico, a conversão
padrão do objectMapper utilziando pelo redis na deserialização apresenta problema.(uma outra solução é configurar serializadores e
decerializadores apropriados e passálos para o objectMapper e redis na inicialização, sobrescrevendo os beans padrões da lib stater)
- Para a listagem por autor e genero como os valores para esses campos da base de dados selecionadas apresenta valores com caracteres
especiais e espaços, a abordagem selecionada foi receber uma string com o nome ou parte do nome do autor/genero que é permitida
num path parameter e feita uma busca onde verifica se no campo autor ou genero se o texto informado no caminho da url é encontrado
em partes na base de dados.
- O cache para a lista de autores e genero é feita utilizando a anotação do spring, o @Cacheble, evitando a necessidade
de uso do port de cache. (apesar disos gerar um acoplamento ao framework na camada de aplicação no caso de uso, foi utilizado
devido a praticidade e tempo curto para entrega do projeto, além de demonstrar uma outra forma de fazer o cache que não utilizando
o redisTemplate diretamente)
- Já para os útimos acessados foi adicionado um parametro de query para informar o id do usuário toda vez que for fazer uma busca por id,
com isso a lista de últimos acessados é gerada via cache com base no usuário. Aqui a lista de acessados recetemente faz uso de 
duas estruturas de dados, uma lista operando no modelo FIFO (first in first out), e um hash. Defini que teria no máximo
10 registro dos últimos acessados, a lista FIFO registra os últimos 10 IDs dos livros buscados por ID (remove caso exista, antes de adicionar
na lista) e como a adição é sempre feita na esquerda e a remoção na direitoa garante a ordem de visualização do mais recente para o mais antigo,
e no hash é salvo os livros em si, onde a chave do hash de cada registro é o ID do livro.

## Melhorias e Considerações Finais

- A listagem de livros por genero e autor poderiam ser ajustadas para também retornar os dados paginados;
- O cache das buscar por genere e autor poderiam ter uma abordagem parecida com a utilizada na listagem de ultimos visualizados,
onde teriamos uma lista com os IDs cacheados daquela busca e uma tabela HASH com os livros em si. Com isso evitariamos duplicar livros
no cache por autor e genero, essa abordagem apesar de trazer eficiência para uso de espaço de memória do cache, precisa ser bem gerenciada
em especial quando ocorrer o cache evction, pois precisamos garantir que os IDs que estão na listas cacheadas estejam disponíveis no HASH,
ou ter um mecanisco de quando o ID da lista não estiver no cache, ir buscar apenas aquele no banco de dados;
- A API poderia permitir cadastro e edição de livros, tornando ela mais usual e mais completa em suas funcionalidades;
  