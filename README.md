## Para cada tipo de dados h� um layout diferente.
* Dados do vendedor: Os dados do vendedor t�m o formato id 001 e a linha ter� o seguinte formato: 001�CPF�Name�Salary

* Dados do cliente: Os dados do cliente t�m o formato id 002 e a linha ter� o seguinte formato: 002�CNPJ�Name�Business Area

* Dados de vendas: Os dados de vendas t�m o formato id 003. Dentro da linha de vendas, existe a lista de itens, que � envolto por colchetes []. A linha ter� o seguinte formato: 003�Sale ID�[Item ID-Item Quantity-Item Price]�Salesman name

### Dados de Exemplo: 

O seguinte � um exemplo dos dados que o sistema deve ser capaz de ler.

    001�1234567891234�Pedro�50000
    001�3245678865434�Paulo�40000.99
    002�2345675434544345�Jose da Silva�Rural
    002�2345675433444345�Eduardo Pereira�Rural
    003�10�[1-10-100,2-30-2.50,3-40-3.10]�Pedro
    003�08�[1-34-10,2-33-1.50,3-40-0.10]�Paulo

An�lise de dados

* Sistema deve ler dados do diret�rio padr�o, localizado em %HOMEPATH%/data/in.
* O sistema deve ler somente arquivos .dat.
* Depois de processar todos os arquivos dentro do diret�rio padr�o de entrada, o sistema deve criar um arquivo dentro do diret�rio de sa�da padr�o, localizado em %HOMEPATH%/data/out.
* O nome do arquivo deve seguir o padr�o, {flat_file_name} .done.dat.

O conte�do do arquivo de sa�da deve resumir os seguintes dados:

* Quantidade de clientes no arquivo de entrada
* Quantidade de vendedor no arquivo de entrada
* ID da venda mais cara
* O pior vendedor
* O sistema deve estar funcionando o tempo todo.
* Todos os novos arquivos dispon�veis devem ser executado

