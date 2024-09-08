package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

import compilerTools.Directory;
import compilerTools.ErrorLSSL;
import compilerTools.Production;
import compilerTools.TextColor;
import compilerTools.Token;

public class controller {
	public String title;
  public Directory directory;
  public ArrayList<Token> token;
  public ArrayList<ErrorLSSL> errors;
  public ArrayList<TextColor> textColor;
  private Timer tmekeyRelased;
  private ArrayList<Production> idnetpro; //para el analisis sintatictic
  private HashMap<String, String> indentificadores;
  private boolean compiled=false;

  public controller(){
    init();
  }
  public void init(){
    title="complador tec";
    directory= new Directory(this, jtpCode , title, ".AB");

  }

}
