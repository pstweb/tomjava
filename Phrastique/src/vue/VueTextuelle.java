package vue;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JTextPane;

import modele.Recuperation;


public class VueTextuelle extends Vue{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Creer le JPanel qui contient la vue Textuelle, ce JPanel a une taille defini en statique
	 * ainsi que les deux JPanel qui contiendront les phrases et les relations
	 */
	public VueTextuelle(){
		super();
		reDessinerPhrases();
		reDessinerRelations(this.getDonnees());
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
	
		for(Component comp : this.getComponents()){
			if(comp instanceof JTextPaneRelation){
								
				/**
				 * Cr�er une bordure arrondi pour chaque JTextPane de Relation
				 */
				BordureArrondi bord = new BordureArrondi(((JTextPaneRelation) comp).getRelation().getCouleur());
				bord.paintBorder(comp, g, comp.getX(), comp.getY(), comp.getWidth(), comp.getHeight());
				/***/
				passerPointille(g);
				int j = 0;
				Component phrase1 = null;
				Component phrase2 = null;
				/**
				 * recherche les phrases 
				 */
				do {
					if(this.getComponent(j) instanceof JTextPanePhrase){
						if (((JTextPanePhrase)this.getComponent(j)).getPhrase().getId().equals(((JTextPaneRelation)comp).getRelation().getIdCible())) {
							phrase2 = this.getComponent(j);
						}
						if (((JTextPanePhrase)this.getComponent(j)).getPhrase().getId().equals(((JTextPaneRelation)comp).getRelation().getIdSource())) {
							phrase1 = this.getComponent(j);
						}
					}
					j++;
				}while(!(this.getComponent(j) instanceof JTextPane) || phrase1 == null || phrase2 == null );
				
				if(comp.getX() < phrase1.getX()){
					g.drawLine(comp.getX()+comp.getWidth(), comp.getY()+comp.getHeight()/2-5,
							phrase1.getX(), phrase1.getY()+phrase1.getHeight()/2);
				}
				else{
					g.drawLine(comp.getX(), comp.getY()+comp.getHeight()/2-5,
							phrase1.getX()+phrase1.getWidth(), phrase1.getY()+phrase1.getHeight()/2);
				}
				
				if(comp.getX() < phrase2.getX()){
					g.drawLine(comp.getX()+comp.getWidth(), comp.getY()+comp.getHeight()/2-5,
							phrase2.getX(), phrase2.getY()+phrase2.getHeight()/2);
				}
				else{
					g.drawLine(comp.getX(), comp.getY()+comp.getHeight()/2-5,
							phrase2.getX()+phrase2.getWidth(), phrase2.getY()+phrase2.getHeight()/2);
				}
				passerPlein(g);
			}
		}
		
	}
	
	/**
	 * @param g
	 * @return
	 * 
	 * Passe l'objet Graphics g pour qu'il dessine plus large et en pointill�
	 */
	public Graphics2D passerPointille(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		float epaisseur = 3; /** taille de la ligne */
		float[] style = {10,5}; /** les pointill�s seront 2 fois plus long que les blancs */
		g2d.setStroke( new BasicStroke(epaisseur, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
				10.0f, style, 0 ));
		return g2d;
	}
	
	/**
	 * @param g
	 * @return
	 * 
	 * repasse l'objet Graphics g en mode normal
	 */
	public Graphics passerPlein(Graphics g){
		Graphics2D g2d = (Graphics2D)g;
		g2d.setStroke(new BasicStroke());
		return g;
	}
	
	/**
	 * @param donnees
	 * 
	 *  cr�er pour chaque phrase du mod�le donn� en param�tre un JTextPane qui contient son id et
	 *  la phrase et positionne celui-ci au centre du JPanel
	 */
	public void reDessinerPhrases(){
		LinkedList<JTextPanePhrase> listePhrase = this.getComposantsGraphiquePhrases();
		int posY = 50;
		for(int i=0; i<listePhrase.size(); i++){
			int nbLignes = listePhrase.get(i).getText().length()/81 + 1;
			listePhrase.get(i).setBounds(this.getTaillePanel().width/2-this.getLargeurPhrase()/2, posY, this.getLargeurPhrase(),
					listePhrase.get(i).getPreferredSize().height*nbLignes - (nbLignes-1)*12);
			posY += listePhrase.get(i).getBounds().height+getEspacementPhrase();
		}
	}
	
	/**
	 * @param donnees
	 * 
	 * Cr�� une Hashtable reliant chaque id de phrase au JTextPane lui correspondant. Cr�� un 
	 * JTextPane contenant les tags de chaque Relation du mod�le donn� en param�tre puis les 
	 * positionnent sur le JPanel alternativement � gauche et � droite des phrases et au milieu
	 * des deux phrases.  
	 */
	public void reDessinerRelations(Recuperation donnees){
		LinkedList<JTextPaneRelation> listeRelation = this.getComposantsGraphiqueRelation();
		LinkedList<JTextPanePhrase> listePhrase = this.getComposantsGraphiquePhrases();
		JTextPanePhrase phraseSource = listePhrase.get(0);
		JTextPanePhrase phraseCible = listePhrase.get(0);
		
		boolean position = true; // position a gauche
		for(int i=0;i<listeRelation.size(); i++){
			for(int j=0;j<listePhrase.size(); j++){
				if(listeRelation.get(i).getRelation().getIdSource().equals(listePhrase.get(j).getPhrase().getId())){
					phraseSource = listePhrase.get(j);
				}
				if(listeRelation.get(i).getRelation().getIdCible().equals(listePhrase.get(j).getPhrase().getId())){
					phraseCible = listePhrase.get(j);
				}
			}
			int nbLignes = listeRelation.get(i).getText().length()/30 + 1;
			if(position == true){
				listeRelation.get(i).setBounds(this.getTaillePanel().width/4-this.getLargeurRelation()/2,
						(phraseSource.getY()+phraseSource.getHeight()+phraseCible.getY())/2
						-(listeRelation.get(i).getPreferredSize().height*nbLignes-(nbLignes-1)*4)/2 , this.getLargeurRelation(), 
						listeRelation.get(i).getPreferredSize().height*nbLignes - (nbLignes-1)*4);
				position = false;
				
			}else{
				listeRelation.get(i).setBounds(this.getTaillePanel().width*3/4-this.getLargeurRelation()/2,
						(phraseSource.getY()+phraseSource.getHeight()+phraseCible.getY())/2
						-(listeRelation.get(i).getPreferredSize().height*nbLignes-(nbLignes-1)*4)/2 , this.getLargeurRelation(), 
						listeRelation.get(i).getPreferredSize().height*nbLignes - (nbLignes-1)*4+5);
				position = true;
			}
		}
	}
}
