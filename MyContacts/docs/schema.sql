-- Tabela usada pelo MyContacts (SQLite).
-- A aplicação cria essa tabela sozinha na primeira execução (Conexao.java).
-- Obs: no SQLite a palavra é AUTOINCREMENT (sem o "_" do MySQL).
CREATE TABLE IF NOT EXISTS contatos (
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    nome     VARCHAR(100) NOT NULL,
    telefone VARCHAR(20),
    email    VARCHAR(100),
    empresa  VARCHAR(100)   -- pode ser nulo (nulo = contato pessoal)
);
