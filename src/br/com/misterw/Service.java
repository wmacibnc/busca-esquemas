package br.com.misterw;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Service {

	public List<Arquivo> obterArquivosPorNome (String diretorio, String nome, Boolean exibeExtensao){
		List<Arquivo> lista = new ArrayList<Arquivo>();
		File file = new File(diretorio);
		File afile[] = file.listFiles();
		int i = 0;
		for (int j = afile.length; i < j; i++) {
			File arquivo = afile[i];
			String nomeArquivo = arquivo.getName();
			if(arquivo.isFile() && nomeArquivo.contains(nome)){
				
				if(!exibeExtensao){
					String ext[] = nomeArquivo.split("\\."); 
					nomeArquivo = ext[0];
				}
				lista.add(new Arquivo(nomeArquivo, arquivo.getPath()));				
			}
		}
		return lista;
	}
}
