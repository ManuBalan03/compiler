package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

import compilerTools.Token;

public class triplos {
  public int cont;
  public int contCadena;
  private ArrayList<TokenInfo> tokens;
private static TreeMap<String, Integer> MAPOperaciones;
private static TreeMap<String, Integer> MAPoperator ;

public triplos(){
  init();
}
public void init() {
}
static {
        MAPOperaciones = new TreeMap<>(Collections.reverseOrder());
        MAPOperaciones.put("!=", 1);
        MAPOperaciones.put("==", 1);
        MAPOperaciones.put("<=", 1);
        MAPOperaciones.put(">=", 1);
        MAPOperaciones.put("<", 1);
        MAPOperaciones.put(">", 1);
        MAPOperaciones.put("=", 1);
        MAPOperaciones.put("+", 2);
        MAPOperaciones.put("-", 2);
        MAPOperaciones.put("/", 3);
        MAPOperaciones.put("*", 3);
        MAPOperaciones.put("%", 3);
    }
    static {
        MAPoperator = new TreeMap<>(Collections.reverseOrder());
        MAPoperator.put("!=", 1);
        MAPoperator.put("==", 1);
        MAPoperator.put("<=", 1);
        MAPoperator.put(">=", 1);
        MAPoperator.put("<", 1);
        MAPoperator.put(">", 1);
        MAPoperator.put("=", 1);
        MAPoperator.put("+", 2);
        MAPoperator.put("-", 3);
        MAPoperator.put("/", 4);
        MAPoperator.put("*", 5);
        MAPoperator.put("%", 6);
    }
    public ArrayList<Object[]> datos(int startIndex, int line, String Identificador, ArrayList<TokenInfo> tokens,int cont) {
      this.cont=cont;
      ArrayList<Object[]> rows = new ArrayList<>();
      this.tokens = tokens;
      String[] cadena = CadenaGet(startIndex, line);
      String Tpos[][]= new String[40][2];
      int[][] Jerarquia = Jerarquias(cadena);
      int[] CantJerarquia = CantforJerarquia(Jerarquia, cadena.length);
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
        boolean triplofinal= isFinalTripleForm(cadena);
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
            String tempMenor = buscarTemporalMenor(Tpos, temporalActual);
            
            Object[] fila1 = new Object[4];
            fila1[0] = cont++;
            fila1[1] = temporalActual;
            fila1[2] = (operandoIzq=="")? tempMenor:operandoIzq;
            fila1[3] = "=";
            rows.add(fila1);
        }
          if (lastcadena(cadena) != null && inicializateTemporal==false) {
              cadena = lastcadena(cadena);
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

      contCadena = 0;
      return rows;
  }
  
  public int[] CantforJerarquia(int[][] Jerarquia , int size) {
    int cant []= new int[3];
    for (int i = 0; i < size; i++) {
      if (Jerarquia[i][2]==3) {
        cant[2]++;
      }
      else if(Jerarquia[i][2]==2){
        cant[1]++;
      }
      else if (Jerarquia[i][2]==1) {
        cant[0]++;
      }
  }
    return cant;
  }
  public void fillTable( )
  {
  }

  public String[] CadenaGet(int startIndex, int line){
        TokenInfo token;
        int cant= 0;
        for (int i = startIndex; i < tokens.size(); i++) {
            token = tokens.get(i);
            if (token.getLine() == line){
                cant++;
            }
        }
        String array[]=new String[cant];
        cant=0;
        for (int i = startIndex; i < tokens.size(); i++) {
            token = tokens.get(i);
            String lexeme= token.getLexeme();

            if (token.getLine() == line){
                array[cant]=lexeme;
                cant++;
            }
        }
        return array;
    }

  public int[][] Jerarquias(String [] cadena){
    int [][] CantJerarquia= new int [40][3];
    int number=0;
    int operador=0;
    for(int i=0; i<cadena.length;i++){
      try {
       number= MAPOperaciones.get(cadena[i]);
       operador=MAPoperator.get(cadena[i]);
      } catch (Exception e) {
        number=0;
        operador=0;
      }
      switch (number) {
        case 1:
            CantJerarquia[i][0]=operador;
            CantJerarquia[i][1]=i;
            CantJerarquia[i][2]=1;
          break;
        case 2:
            CantJerarquia[i][0]=operador;
            CantJerarquia[i][1]=i;
            CantJerarquia[i][2]=2;
          break;
        case 3:
            CantJerarquia[i][0]=operador;
            CantJerarquia[i][1]=i;
            CantJerarquia[i][2]=3;
          break;
        default:
          break;
      }
    }
    return order(CantJerarquia);
  }
  public int[][] order(int [][] matriz){
    Arrays.sort(matriz, new Comparator<int[]>() {
            @Override
            public int compare(int[] a, int[] b) {
                return Integer.compare(b[2], a[2]); 
            }
        });
    return matriz;
  }
  public String[] lastcadena(String [] cadena){
    String []last= new String[3];
    int cont=0;
    for(int i=0; i<cadena.length;i++){
      if (cadena[i]!="") {
        cont++;
      }
    }
    boolean size= (cont>3)? true : false;
    if (size) {
      return null;
    }
    cont=0;
    for(int i=0; i<cadena.length;i++){
      if (cadena[i]!="") {
        last[cont]=cadena[i];
        cont++;
      }
    }
    return last;
  }

  public boolean isFinalTripleForm(String[] cadena) {
    int tempCount = 0;
    int opCount = 0;
    
    for (String item : cadena) {
        if (item == null || item.isEmpty()) {
            continue;
        }
        if (item.startsWith("T")) { 
            tempCount++;
        } else if (MAPOperaciones.containsKey(item)) { 
            opCount++;
        }
    }
    
    return tempCount == 2 && opCount == 1;
}


public String buscarTemporalMenor(String[][] temp, String temporalActual) {
  
  // Encontrar la posición del temporal actual
  int posicionActual = -1;
  for (int i = 0; i < temp.length && temp[i][0] != null; i++) {
      if (temp[i][0].equals(temporalActual)) {
          posicionActual = Integer.parseInt(temp[i][1]);
          break;
      }
  }
  
  if (posicionActual == -1) {
      return null;
  }
  
  String temporalMenor = null;
  int posicionMenor = Integer.MAX_VALUE;
  
  // Buscar el temporal con la posición más cercana pero menor a la actual
  for (int i = 0; i < temp.length && temp[i][0] != null; i++) {
      if (temp[i][1] != null) {
          int posicionCandidata = Integer.parseInt(temp[i][1]);
          // La posición debe ser menor que la actual pero mayor que cualquier otra encontrada
          if (posicionCandidata < posicionActual && posicionCandidata < posicionMenor) {
              posicionMenor = posicionCandidata;
              temporalMenor = temp[i][0];
          }
      }
  }
  return temporalMenor;
}
  public int get(){
    return cont;
  }
  public void set(int cont){
    this.cont=cont;
  }
}
