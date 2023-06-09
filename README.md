# Parking Spot API

- Projeto de API voltado a administração de vagas de carro em um condominio onde o usuário insere: número da vaga, placa, cor e marca do carro, nome do responsável, bloco e apartamento.
 
- O sistema gera um ID e uma Data de registro para cada lançamento. 
 
- No momento da inserção da solitação é analisado se a vaga está vazia, se já existe um registro para aquele apartemento + bloco ou para a placa do carro (seguindo a regra de que cada condômino pode fazer uso de apenas uma vaga).
 
- Os dados são armazenados em bando MySQL e é possível realizar inserir, apagar, deletar e pesquisar solicitações. A pesquisa foi dividida em paginações e também pode ser feita utilizando ID do registro.
