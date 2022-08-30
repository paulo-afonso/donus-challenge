# Desafio de Backend

Projeto desafio de candidatura de vaga. O projeto consiste numa simulação de criação de contas e realização de depósito e transferência.

### Endpoints:

#### **Criação: ('/create')**
#### Para criação de uma conta são necessários:
- Nome
- CPF de código único que esteja de acordo com os critérios de validação, com ou sem pontuação ('.' e '-')
- Uma senha com obrigatoriamente 6 números, que é opicional (uma senha aleatória é gerada caso o usuário não escolha)


<img src="https://user-images.githubusercontent.com/88559780/187359888-f16449ab-1310-4390-b17e-903b3355d907.png" width="250"/> |
<img src="https://user-images.githubusercontent.com/88559780/187360114-f6d99d7e-edcc-4732-948c-388abb4a589a.png" width="252"/>

#### **Depósito: ('/deposit')**
#### Para depositar numa conta é necessário:
- O CPF de uma conta já existente
- Um valor de depósito positivo e diferente de zero, com quantia máxima de até 2.000

<img src="https://user-images.githubusercontent.com/88559780/187361239-eff8f810-b32d-4ef2-8e9e-a14f10c577a7.png" width="250"/> |
<img src="https://user-images.githubusercontent.com/88559780/187362208-1a4064ef-ba87-4d1c-acee-b86f0fb70511.png" width="400"/>


#### **Transferência: ('/transfer')**
#### Para transferir para uma conta é necessário:
- O CPF do remetente de uma conta válida
- A senha da conta do remetente
- O CPF do destinatário de uma conta válida
- Um valor de transferência igual ou menor que o saldo do remetente, que seja positivo e diferente de zero, com quantia máxima de até 2.000


<img src="https://user-images.githubusercontent.com/88559780/187362686-1df0b83f-efd7-491e-971f-cc55229d9349.png" width="250"/> |
<img src="https://user-images.githubusercontent.com/88559780/187362822-b3fede70-5b0b-4f90-8deb-cadce5008659.png" width="400"/>


### Próximas implementações
- Validação de login por token JWT
- Deploy no heroku e documentação no swagger
- Endpoint de relatório dos depósitos e transferências por usuário

### Escalabilidade
- Um possível aumento na quantidade de usuários poderia demandar o uso do serviço de cache (e.g, Redis) para melhoria na perfomace das requisições.
- A implementação de um serviço de mensageria como RabbitMQ poderia reforçar a segurança do histórico de transações para potencial necessidade de validação.
