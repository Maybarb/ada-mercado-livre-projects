class Aluno:
    """Classe que representa um aluno com suas informações básicas."""
    
    def __init__(self, nome, matricula, idade, turma):
        """
        Inicializa um objeto Aluno.
        
        Args:
            nome (str): Nome do aluno
            matricula (str): Número de matrícula do aluno
            idade (int): Idade do aluno
            turma (str): Turma do aluno
        """
        self.nome = nome
        self.matricula = matricula
        self.idade = idade
        self.turma = turma
    
    def __str__(self):
        """Retorna uma representação em string do aluno."""
        return f"Aluno: {self.nome} | Matrícula: {self.matricula} | Idade: {self.idade} | Turma: {self.turma}"
    
    def __repr__(self):
        """Retorna a representação oficial do objeto."""
        return f"Aluno('{self.nome}', '{self.matricula}', {self.idade}, '{self.turma}')"
    
    def exibir_info(self):
        """Exibe as informações do aluno de forma formatada."""
        print(f"Nome: {self.nome}")
        print(f"Matrícula: {self.matricula}")
        print(f"Idade: {self.idade}")
        print(f"Turma: {self.turma}")


# Exemplo de uso
if __name__ == "__main__":
    # Criando um aluno de teste
    aluno1 = Aluno("João Silva", "2024001", 18, "1A")
    print("=== Informações do Aluno ===")
    aluno1.exibir_info()
    print("\n" + str(aluno1))
