package br.com.misterw;

public class Arquivo {

	private String nome;
	private String local;

	public Arquivo() {
		super();
	}

	public Arquivo(String nome, String local) {
		super();
		this.nome = nome;
		this.local = local;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	@Override
	public String toString() {
		return this.nome + " - " + this.local;
	}
}
