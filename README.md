# Desafio de Backend

Projeto desafio de candidatura de vaga. O projeto consiste numa simulação de criação de contas e realização de depósito e transferência.

### Endpoints:

#### **Criação: ('/account/create')**
#### Para criação de uma conta são necessários:
- Nome
- CPF de código único que esteja de acordo com os critérios de validação, com ou sem pontuação ('.' e '-')
- Uma senha com obrigatoriamente 6 números, que é opicional (uma senha aleatória é gerada caso o usuário não escolha)


<img src="https://user-images.githubusercontent.com/88559780/187524844-92b31199-3b5b-488c-a599-7ce161e1f1c9.png" width="250"/> |
<img src="https://user-images.githubusercontent.com/88559780/187525041-3f16689f-68db-4436-adfb-fa0acd16f9cd.png" width="255"/>


#### **Depósito: ('/account/deposit')**
#### Para depositar numa conta é necessário:
- O CPF de uma conta já existente
- Um valor de depósito positivo e diferente de zero, com quantia máxima de até 2.000

<img src="https://user-images.githubusercontent.com/88559780/187525384-edae30ae-1230-409e-9074-67552401e798.png" width="250"/>


#### **Transferência: ('/account/transfer')**
#### Para transferir para uma conta é necessário:
- O CPF do remetente de uma conta válida
- A senha da conta do remetente
- O CPF do destinatário de uma conta válida
- Um valor de transferência igual ou menor que o saldo do remetente, que seja positivo e diferente de zero


<img src="https://user-images.githubusercontent.com/88559780/187525668-82c10b64-f6ff-4ccf-bd6c-3e18e2690eca.png" width="250"/>



### Próximas implementações
- Validação de login por token JWT
- Deploy no heroku e documentação no swagger
- Endpoint de relatório dos depósitos e transferências por usuário

### Escalabilidade
- Um possível aumento na quantidade de usuários poderia demandar o uso do serviço de cache (e.g, Redis) para melhoria na perfomace das requisições.
- A implementação de um serviço de mensageria como RabbitMQ poderia reforçar a segurança do histórico de transações para potencial necessidade de validação.
