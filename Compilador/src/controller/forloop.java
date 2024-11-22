package controller;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.StyledEditorKit.BoldAction;

import compilerTools.Token;
import controller.Compilador;
public class forloop {
  private ArrayList<Token> tokens;
  public int contCadena;
  public int cont;
  private triplos obj =new triplos();
  int [] array;
  public forloop(){
  }
  public void init(){
    cont=0;
  }
    public ArrayList<Object[]> analyzeForLoop(int start, ArrayList<Token> tokens, int line, int last,int cont){
      init();
      this.cont=cont;
      this.tokens = tokens;
      String cadena[]= CadenaGet(start, line);
      ArrayList<Object[]> intrucciones = new ArrayList<>();
      int contador[]=posicionescoma(cadena);
      String identificadorVI=getIdentificador(cadena, contador[0],0,true);
      String valorInicial[]= VInicial(cadena, contador[0],0,true);
      valorInicial=removeFirstTwoElements(valorInicial);

      String Condicion[]= VInicial(cadena, contador[1],contador[0],false);
      String Condicion1[]= VInicial(cadena, contador[1],contador[0],false);
      //LISTA 1 VA AL PRINCIPIO

      ArrayList<Object[]> lista1 =datos(valorInicial,identificadorVI,false);
      Object[] ultimaFila = lista1.get(lista1.size() - 1);
      int valorPrimeraColumna = Integer.parseInt(ultimaFila[0].toString());
      ArrayList<Object[]> mergedList = new ArrayList<>(lista1);
      int contCond[] = cantCondiciones(Condicion);
      
      int posicionesCondicionesp[] = posicionescodn(Condicion, contCond[0] + contCond[1]);
      Boolean there= (contCond[0]+contCond[1]==0)?true:false;
      Boolean operationAND=(there==false&&contCond[0]!=0)?true:false;
      int inicioBody =processAndCountConditions(Condicion);
      inicioBody+=valorPrimeraColumna+1;
      valorInicial=removeFirstTwoElements(valorInicial);
      
      String identificandoInt=getIdentificador(cadena, cadena.length,contador[1],false);
      String Intervalo[]= VInicial(cadena, cadena.length, contador[1],false);
      String Interval1[]= VInicial(cadena, cadena.length, contador[1],false);
      Intervalo=operadorEsp(Intervalo);
      Intervalo=removeFirstTwoElements(Intervalo);
      Interval1=operadorEsp(Interval1);
      Interval1=removeFirstTwoElements(Interval1);
      int taminter= datos(Intervalo, identificandoInt, true).size();
      int resta=last-line;
      resta--;
      int finalbucle=getcantbody(resta,start,line);
      finalbucle+=inicioBody;
      finalbucle+=taminter+1;
     

      // Verificamos y obtenemos identificadores condicionales
      //AQUI LAS CONDICCIONES VAN DESPUES DEL INICIO
      if (contCond[0] + contCond[1]==0) {
        String[] identificadorCond= new String[2];
        identificadorCond[0]=Condicion1[0];
        identificadorCond[1]=Condicion1[1];
        mergedList.addAll(datosCond(Condicion1, identificadorCond,there,operationAND,false,inicioBody, finalbucle,0));
      }
      else{
        for (int i = 0; i < contCond[0] + contCond[1] + 1; i++) {  
          String [] identificadorCond;
          String [] condicionNew;
      
          if (i == 0) {
              // Primer identificador antes del primer operador
              identificadorCond = getIdentificadorCond(Condicion, posicionesCondicionesp[0], 0);
              condicionNew=getcondicionales(Condicion, posicionesCondicionesp[0], 0);
              mergedList.addAll(datosCond(condicionNew, identificadorCond,there,operationAND,false,inicioBody, finalbucle,i));
          } else if (i == contCond[0] + contCond[1]) {
              // Último identificador después del último operador
              identificadorCond = getIdentificadorCond(Condicion, Condicion.length, posicionesCondicionesp[i - 1] + 1);
              condicionNew=getcondicionales(Condicion,Condicion.length, posicionesCondicionesp[i - 1] + 1);
              mergedList.addAll(datosCond(condicionNew, identificadorCond,there,operationAND,false,inicioBody, finalbucle,i));
          } else {
              // Identificadores intermedios entre operadores
              identificadorCond = getIdentificadorCond(Condicion, posicionesCondicionesp[i], 
              posicionesCondicionesp[i - 1] + 1);
              condicionNew=getcondicionales(Condicion, posicionesCondicionesp[i], 
              posicionesCondicionesp[i - 1] + 1);
              mergedList.addAll(datosCond(condicionNew, identificadorCond,there,operationAND,false,inicioBody, finalbucle,i));
          }
      }
      }
     
      
      for (int i = 0; i < resta; i++){
        ArrayList<Object[]> linea;
        line++;
        String [] condicionNew=CadenaGet(start, line);
        String identificadorCond=condicionNew[0];
        condicionNew=removeFirstTwoElements(condicionNew);
        linea=datos(condicionNew,identificadorCond,false);
        intrucciones.addAll(linea);
    }
       //BLOQUE DE CODIGO VA AL MEDIO
       mergedList.addAll(intrucciones);
        //INTERVALO VA AL FINAL
      ArrayList<Object[]> lista2 =datos(Interval1, identificandoInt, false);
      mergedList.addAll(lista2);
      mergedList.addAll(jump(valorPrimeraColumna));
      return mergedList;
    }
    public int getcantbody(int resta, int start, int line){
      ArrayList<Object[]> linea= new ArrayList<>();
      int cant=0;
      for (int i = 0; i < resta; i++){
        
        line++;
        String [] condicionNew=CadenaGet(start, line);
        String identificadorCond=condicionNew[0];
        condicionNew=removeFirstTwoElements(condicionNew);
        linea=datos(condicionNew,identificadorCond,true);
        cant+=linea.size();
    }
      return cant;
    }
    private ArrayList<Object[]>  jump(int valor) {
      Object[] fila = new Object[4];
      fila[0] = cont++;
      fila[1] = "";
      fila[2] = valor+1;
      fila[3] = "JUMP";
      ArrayList<Object[]> lista2  = new ArrayList<>();
      lista2.add(fila);
      return lista2;
    }
    private String[] operadorEsp(String[] cadena) {
      boolean thereis=false;
      for(int i=0; i<cadena.length;i++){
        if (cadena[i].matches("\\+\\+")) {
          thereis=true;
        }
      }
      String identificador= cadena[0];
      if (thereis) {
        cadena= new String[5];
        cadena[0]=identificador;
        cadena[1]="=";
        cadena[2]=identificador;
        cadena[3]="+";
        cadena[4]="1";
        return cadena;
      }
      else{
        return cadena;
      }
    }
    public String getIdentificador(String []cadena, int contador, int start , boolean ValorInicial){
      int contCadena=0;
      int startwith=(ValorInicial)?0:1;
      for (int i = start+startwith; i < contador; i++) {
        
        contCadena++;
      }
      String []CadenaNew= new String[contCadena];
      contCadena=0;
      for (int i = start+startwith; i < contador; i++) {
        CadenaNew[contCadena]=cadena[i];
        contCadena++;
      }
      String identificador= CadenaNew[0];
      return identificador;
    }
    public String[] getIdentificadorCond(String []cadena, int contador, int start ){
      String identificador[]= new String[2];
      int contCadena=0;
      for (int i = start; i < contador; i++) {
        
        contCadena++;
      }
      String []CadenaNew= new String[contCadena];
      contCadena=0;
      for (int i = start; i < contador; i++) {
        CadenaNew[contCadena]=cadena[i];
        contCadena++;
      }
      identificador[0]= CadenaNew[0];
      identificador[1]= CadenaNew[1];
      return identificador;
    }
    private String[] VInicial(String []cadena, int contador, int start,boolean ValorInicial) {
      int startwith=(ValorInicial)?0:1;
      int contCadena=0;
      for (int i = start+startwith; i < contador; i++) {
        contCadena++;
      }
      String []CadenaNew= new String[contCadena];
      contCadena=0;
      for (int i = start+startwith; i < contador; i++) {
        CadenaNew[contCadena]=cadena[i];
        contCadena++;
      }
      return CadenaNew;
    }
    public int[] posicionescoma(String []cadena){
      int [] contador= new int[3];
      int contC=0;
      for (int i = 0; i < cadena.length; i++) {
            if (cadena[i].matches(";")) {
              contador[contC]=i;
              contC++;
            }
      }
      return contador;
    }
    public int[] posicionescodn(String []cadena , int cant){
      
      int [] contador= new int[cant];
      int contC=0;
      for (int i = 0; i < cadena.length; i++) {
            if (cadena[i].matches("&&")||cadena[i].matches("\\|\\|")) {
              contador[contC]=i;
              contC++;
            }
      }
      return contador;
    }
    public String[] CadenaGet(int startIndex, int line){
      Token token;
      int cant= 0;
      for (int i = startIndex; i < tokens.size(); i++) {
          token = tokens.get(i);
          String lexeme= token.getLexeme();
          if (token.getLine() == line){
            if(lexeme.matches(".*[(){}]|for.*")==false){
              cant++;
            }
              
          }
      }
      String array[]=new String[cant];
      cant=0;
      for (int i = startIndex; i < tokens.size(); i++) {
          token = tokens.get(i);
          String lexeme= token.getLexeme();

          if (token.getLine() == line){
            if(lexeme.matches(".*[(){}]|for.*")){
            }
            else{
              array[cant]=lexeme;
              cant++;
            }
          }
      }
      return array;
  }
  private String[] removeFirstTwoElements(String[] array) {
    if (array.length <= 2) {
        return new String[0];
    }
    String[] newArray = new String[array.length - 2];
    System.arraycopy(array, 2, newArray, 0, newArray.length);

    return newArray;
}
  public ArrayList<Object[]> datos(String [] cadena, String Identificador,boolean get) {
    ArrayList<Object[]> rows = new ArrayList<>();
    String Tpos[][]= new String[40][2];
    int[][] Jerarquia = obj.Jerarquias(cadena);
    int inicio= cont;
    int[] CantJerarquia = obj.CantforJerarquia(Jerarquia, cadena.length);
    String temporalAnterior = Identificador;
    int tempCounter = 1;
    contCadena = 0; 
    int contlengt=(cadena.length==1)?1:0;
    String temporalActual = "T1";
    boolean inicializateTemporal = true;
    if (contlengt==1) {
        Object[] fila = new Object[4];
        fila[0] = cont++;
        fila[1] = temporalActual;
        fila[2] = cadena[0];
        fila[3] = "=";
        temporalAnterior=temporalActual;
        rows.add(fila);
    }
    for (int i = 0; i < CantJerarquia[0] + CantJerarquia[1] + CantJerarquia[2]; i++) {
      boolean triplofinal= obj.isFinalTripleForm(cadena);
        int resta = 0, resta2 = 0, resta3=0;
        resta = Jerarquia[(i+1)][1] - Jerarquia[i][1];
        
        int posicion = Jerarquia[i][1];
        String operador = cadena[posicion];
        String operandoIzq = (cadena[posicion - 1]=="")?cadena[posicion - 2] : cadena[posicion - 1];
        String operandoDer = cadena[posicion + 1];
        if (i != 0) {
            inicializateTemporal = false;
            resta2 = Jerarquia[i][1] - Jerarquia[(i-1)][1];
            resta3 =  Jerarquia[(i-1)][1] - Jerarquia[i][1];
            temporalAnterior = temporalActual;
            if ((Jerarquia[i][2] == Jerarquia[i + 1][2] || Jerarquia[i][2] == Jerarquia[i - 1][2])
                    && (resta2 > 2 || resta > 2 ||resta3>=2)&& triplofinal==false
                    ) {
                tempCounter++;
                inicializateTemporal = true;
                temporalActual = "T" + tempCounter;
            } else if (Jerarquia[i][2] != Jerarquia[i + 1][2] && (Jerarquia[i][1] > Jerarquia[i + 1][1]) && triplofinal==false && (resta2 > 2 || resta > 2 ||resta3>=2)) {
                tempCounter++;
                temporalActual = "T" + tempCounter;
                inicializateTemporal = true;
            }
        }
        if (inicializateTemporal) {
          // Guardamos el temporal y su posición
          Tpos[contCadena][0] = temporalActual;
          Tpos[contCadena][1] = String.valueOf(posicion);
         
          contCadena++;
          String tempMenor = obj.buscarTemporalMenor(Tpos, temporalActual);
          
          Object[] fila1 = new Object[4];
          fila1[0] = cont++;
          fila1[1] = temporalActual;
          fila1[2] = (operandoIzq=="")? tempMenor:operandoIzq;
          fila1[3] = "=";
          rows.add(fila1);
      }
        if (obj.lastcadena(cadena) != null && inicializateTemporal==false) {
            cadena = obj.lastcadena(cadena);
            operador = cadena[1];
            operandoIzq = cadena[0];
            operandoDer = cadena[2];
            Object[] fila = new Object[4];
            fila[0] = cont++;
            fila[1] = operandoIzq;
            fila[2] = operandoDer;
            fila[3] = operador;
            rows.add(fila);
            if (triplofinal) {
              temporalAnterior=operandoIzq;
            }
            else{
              temporalAnterior=temporalActual;
            }
            break;
        } else {
            cadena[posicion - 1] =temporalActual;
            cadena[posicion] = "";
            cadena[posicion + 1] = "";
            temporalAnterior=temporalActual;
        }
        Object[] fila = new Object[4];
        fila[0] = cont++;
        fila[1] = temporalActual;
        fila[2] = operandoDer;
        fila[3] = operador;
        rows.add(fila);
    }

    // Crear el último triplo para asignación final
    Object[] filaFinal = new Object[4];
    filaFinal[0] = cont++;
    filaFinal[1] = Identificador;
    filaFinal[2] = temporalAnterior;
    filaFinal[3] = "=";
    rows.add(filaFinal);
    if (get) {
      cont=inicio;
    }
    contCadena = 0;
    return rows;
}
public ArrayList<Object[]> datosCond(String [] cadena, String[] Identificador, boolean there,boolean AND, boolean getCant,int inicioBloq , int finalBucle, int PosCondicional) {
 
  int inicio= cont;
  ArrayList<Object[]> rows = new ArrayList<>();
  String Tpos[][]= new String[40][2];
  int[][] Jerarquia = obj.Jerarquias(cadena);
  int[] CantJerarquia = obj.CantforJerarquia(Jerarquia, cadena.length);
  String temporalAnterior = Identificador[0];
  int tempCounter = 1;
  contCadena = 0; 
  int contlengt=(cadena.length==1)?1:0;
  String temporalActual = "T1";
  boolean inicializateTemporal = true;
  if (contlengt==1) {
      Object[] fila = new Object[4];
      fila[0] = cont++;
      fila[1] = temporalActual;
      fila[2] = cadena[0];
      fila[3] = "=";
      temporalAnterior=temporalActual;
      rows.add(fila);
  }
  for (int i = 0; i < CantJerarquia[0] + CantJerarquia[1] + CantJerarquia[2]; i++) {
    boolean triplofinal= obj.isFinalTripleForm(cadena);
      int resta = 0, resta2 = 0, resta3=0;
      resta = Jerarquia[(i+1)][1] - Jerarquia[i][1];
      
      int posicion = Jerarquia[i][1];
      String operador = cadena[posicion];
      String operandoIzq = (cadena[posicion - 1]=="")?cadena[posicion - 2] : cadena[posicion - 1];
      String operandoDer = cadena[posicion + 1];
      if (i != 0) {
          inicializateTemporal = false;
          resta2 = Jerarquia[i][1] - Jerarquia[(i-1)][1];
          resta3 =  Jerarquia[(i-1)][1] - Jerarquia[i][1];
          temporalAnterior = temporalActual;
          if ((Jerarquia[i][2] == Jerarquia[i + 1][2] || Jerarquia[i][2] == Jerarquia[i - 1][2])
                  && (resta2 > 2 || resta > 2 ||resta3>=2)&& triplofinal==false
                  ) {
              tempCounter++;
              inicializateTemporal = true;
              temporalActual = "T" + tempCounter;
          } else if (Jerarquia[i][2] != Jerarquia[i + 1][2] && (Jerarquia[i][1] > Jerarquia[i + 1][1]) && triplofinal==false && (resta2 > 2 || resta > 2 ||resta3>=2)) {
              tempCounter++;
              temporalActual = "T" + tempCounter;
              inicializateTemporal = true;
          }
      }
      if (inicializateTemporal) {
        // Guardamos el temporal y su posición
        Tpos[contCadena][0] = temporalActual;
        Tpos[contCadena][1] = String.valueOf(posicion);
       
        contCadena++;
        String tempMenor = obj.buscarTemporalMenor(Tpos, temporalActual);
        
        Object[] fila1 = new Object[4];
        fila1[0] = cont++;
        fila1[1] = temporalActual;
        fila1[2] = (operandoIzq=="")? tempMenor:operandoIzq;
        fila1[3] = "=";
        rows.add(fila1);
    }
      if (obj.lastcadena(cadena) != null && inicializateTemporal==false) {
          cadena = obj.lastcadena(cadena);
          operador = cadena[1];
          operandoIzq = cadena[0];
          operandoDer = cadena[2];
          Object[] fila = new Object[4];
          fila[0] = cont++;
          fila[1] = operandoIzq;
          fila[2] = operandoDer;
          fila[3] = operador;
          rows.add(fila);
          if (triplofinal) {
            temporalAnterior=operandoIzq;
          }
          else{
            temporalAnterior=temporalActual;
          }
          break;
      } else {
          cadena[posicion - 1] =temporalActual;
          cadena[posicion] = "";
          cadena[posicion + 1] = "";
          temporalAnterior=temporalActual;
      }
      Object[] fila = new Object[4];
      fila[0] = cont++;
      fila[1] = temporalActual;
      fila[2] = operandoDer;
      fila[3] = operador;
      rows.add(fila);
  }
  if (getCant) {
    cont=inicio;
  }
  else{
    if (there) {
      Object[] fila = new Object[4];
       fila[0] = cont++;
       fila[1] = "";
       fila[2] = "true";
       fila[3] =inicioBloq ;
       rows.add(fila);
       Object[] fila1 = new Object[4];
       fila1[0] = cont++;
       fila1[1] = "none";
       fila1[2] = "false";
       fila1[3] =finalBucle ;
       rows.add(fila1);
   }else if(AND){
    int siguiente=0;
      siguiente=array[PosCondicional]+2;
        Object[] fila = new Object[4];
       fila[0] = cont++;
       fila[1] = "and";
       fila[2] = "true";
       fila[3] =siguiente+inicio ;
       rows.add(fila);
      Object[] fila1 = new Object[4];
       fila1[0] = cont++;
       fila1[1] = "";
       fila1[2] = "false";
       fila1[3] =finalBucle ;
       rows.add(fila1);
   }
   else{
    int siguiente=0;
    siguiente=array[PosCondicional]+2;
    siguiente+=inicio;
    if (PosCondicional==array.length-1) {
      siguiente=finalBucle;
    }
        
        Object[] fila = new Object[4];
       fila[0] = cont++;
       fila[1] = "or";
       fila[2] = "true";
       fila[3] =inicioBloq;
       rows.add(fila);
      Object[] fila1 = new Object[4];
       fila1[0] = cont++;
       fila1[1] = "";
       fila1[2] = "false";
       fila1[3] =siguiente;
       rows.add(fila1);
   }
  }
  contCadena = 0;
  return rows;
}
public int[] cantCondiciones(String [] cadenas){
  int [] cant=new int[2];
  for(int i=0;i<cadenas.length;i++){
    if (cadenas[i].matches("&&")) {
      cant[0]++;
    }
    else if (cadenas[i].matches("\\|\\|")) {
      cant[1]++;
    }
  }
  return cant;
}
public String[] getcondicionales(String [] cadena, int termina, int comienza){
  int cont=0;
  for(int i=comienza; i<termina;i++){
    cont++;
  }
  String []newCadena=new String[cont];
  cont=0;
  for(int i=comienza; i<termina;i++){
    newCadena[cont]=cadena[i];
    cont++;
  }
  return newCadena;
}
private int processAndCountConditions(String[] Condicion) {
  // Inicialización y procesamiento de condiciones
  int contCond[] = cantCondiciones(Condicion);
  int posicionesCondicionesp[] = posicionescodn(Condicion, contCond[0] + contCond[1]);
  
  int totalRows = 0;
  int totalextra= contCond[0] + contCond[1] + 1;
  totalextra*=2;
  array= new int[contCond[0] + contCond[1] + 1];
  if ( contCond[0] + contCond[1] ==0) {
          String[] identificadorCond;
          identificadorCond = getIdentificadorCond(Condicion,Condicion.length , 0);
          ArrayList<Object[]> result = datosCond(Condicion, identificadorCond,false, false,true,0,0,0);
          totalRows += result.size();
          array[0]=result.size();
  }
  else{
    for (int i = 0; i < contCond[0] + contCond[1] + 1; i++) {
      String[] identificadorCond;
      String[] condicionNew;
      
      if (i == 0) {
          // Primer identificador antes del primer operador
          identificadorCond = getIdentificadorCond(Condicion, posicionesCondicionesp[0], 0);
          condicionNew = getcondicionales(Condicion, posicionesCondicionesp[0], 0);
          ArrayList<Object[]> result = datosCond(condicionNew, identificadorCond,false, false,true,0,0,0);
          totalRows += result.size();
          array[0]=result.size();
          
      } else if (i == contCond[0] + contCond[1]) {
          // Último identificador después del último operador
          identificadorCond = getIdentificadorCond(Condicion, Condicion.length, posicionesCondicionesp[i - 1] + 1);
          condicionNew = getcondicionales(Condicion, Condicion.length, posicionesCondicionesp[i - 1] + 1);
          ArrayList<Object[]> result = datosCond(condicionNew, identificadorCond, false, false,true,0,0,0);
          totalRows += result.size();
          array[contCond[0] + contCond[1]]=result.size();
          
      } else {
          // Identificadores intermedios entre operadores
          identificadorCond = getIdentificadorCond(Condicion, posicionesCondicionesp[i],
                                                 posicionesCondicionesp[i - 1] + 1);
          condicionNew = getcondicionales(Condicion, posicionesCondicionesp[i],
                                        posicionesCondicionesp[i - 1] + 1);
          ArrayList<Object[]> result = datosCond(condicionNew, identificadorCond, false, false,true,0,0,0);
          totalRows += result.size();
          array[i]=result.size();
      }
  }
  }
  return totalRows+totalextra;
}
}

