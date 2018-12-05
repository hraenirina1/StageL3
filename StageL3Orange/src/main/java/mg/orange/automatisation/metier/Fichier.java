package mg.orange.automatisation.metier;

import java.util.ArrayList;
import java.util.List;

public class Fichier {
	private String nomFichier;
	private List<String> contenu;
	
	public Fichier() {
		
	}
	
	public Fichier(String nomFichier) {
		super();
		this.nomFichier = nomFichier;
		this.contenu = new ArrayList<>();
	}
	
	public void ajouterLigne(String texte)
	{
		contenu.add(texte + "\n");
	}

	public String getNomFichier() {
		return nomFichier;
	}

	public void setNomFichier(String nomFichier) {
		this.nomFichier = nomFichier;
	}


	public String toString()
	{
		String result = new String();
		for (String ligne : contenu) {
			result += ligne;
		}
		
		// formater les caracteres
		result = result.replaceAll("\"","\\\\\"");
		
		return result;
	}

}
