package controller;


import compilerTools.CodeBlock;
import compilerTools.Directory;
import compilerTools.ErrorLSSL;
import compilerTools.Functions;
import compilerTools.Grammar;
import compilerTools.Production;
import compilerTools.TextColor;
import compilerTools.Token;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import controller.*;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JFrame;

import View.view;


public class Compilador {

    private view vista; // Vista que el compilador va a controlar
    int cont;
    private Directory directorio;
    private ArrayList<Token> tokens;
    private ArrayList<TokenInfo> tokeninfo;
    private ArrayList<ErrorLSSL> errors;
    private int N_error;
    private ArrayList<Production> identProd;
    private HashMap<String, String> identificadores;
    private HashMap<String, String> valores_identificadores;
    private HashMap<String, String> ValoresTriplos;
    private boolean codeHasBeenCompiled = false;
    private javax.swing.JTextArea jtaOutputConsole;
    private  String Errorlexeme;
    private Set<String> vistosValor1;
    private HashSet<String> allIdentifiers = new HashSet<>();
    private  String OriginError = "";
    public triplos obj=new triplos();
    public forloop objFor=new forloop();
    public optimizacion objObt= new optimizacion();

    // Constructor
    public Compilador(view vista) {
        this.vista = vista;
        init();
        cont=1;
    }
    public Compilador(){

    }

    private void init() {
        jtaOutputConsole = new javax.swing.JTextArea();
        tokeninfo = new ArrayList<>();
        tokens = new ArrayList<>();
        errors = new ArrayList<>();
        identProd = new ArrayList<>();
        identificadores = new HashMap<>();
        valores_identificadores = new HashMap<>();
        vistosValor1 = new HashSet<>();
        allIdentifiers = new HashSet<>();

        N_error=0;
        Errorlexeme="";
        OriginError = "";
    }

    public void nuevoArchivo() {
        vista.getDirectory().New();
        clearFields();
    }

    public void abrirArchivo() {
        if (vista.getDirectory().Open()) {
            clearFields();
        }
    }

    public void guardarArchivo() {
        if (vista.getDirectory().Save()) {
            clearFields();
        }
    }

    public void guardarArchivoComo() {
        if (vista.getDirectory().SaveAs()) {
            clearFields();
        }
    }

    public void compilarCodigo() {
        directorio =vista.getDirectory();
        vista.setTitle(vista.title());
        if (vista.getTitle().contains("*") || vista.getTitle().equals(vista.title())) {
            if (directorio.Save()) {
                compile();
            }
        } else {
            compile();
        }
        
    }

    private void clearFields() {
        JTable tblTokens = vista.getT_lexemas();
        JTable tblTriplos = vista.getTable_1();
        Functions.clearDataInTable(tblTokens);
        Functions.clearDataInTable(tblTriplos);
        jtaOutputConsole.setText("");
        tokens.clear();
        errors.clear();
        identProd.clear();
        identificadores.clear();
        vistosValor1.clear();
        codeHasBeenCompiled = false;
        valores_identificadores.clear();
    }
    private void compile() {

        clearFields();
        lexicalAnalysis();
        fillTableTokens();
        semanticAnalysis();
        printConsole();
        codeHasBeenCompiled = true;
        removeUndefinedIdentifiers();
        
    }
    private void fillTableTokens() {
        tokens.forEach(token -> {
           String Valor1= token.getLexeme();
           if (!vistosValor1.add(Valor1)) {
        }
           else{
            System.out.println("Token: " + Valor1 + ", Tipo: " + token.getLexicalComp());
            Object[] data = new Object[]{token.getLexeme(), token.getLexicalComp()};
            Functions.addRowDataInTable(vista.getT_lexemas(), data);
           }
        });
    }

    private void printConsole() {
        int sizeErrors = errors.size();
        if (sizeErrors > 0) {
            Functions.sortErrorsByLineAndColumn(errors);
            String strErrors = "\n";
            for (ErrorLSSL error : errors) {
                String strError = String.valueOf(error);
                strErrors += strError + "\n";
            }
            jtaOutputConsole.setText("Compilación terminada...\n" + strErrors + "\nLa compilación terminó con errores...");
        } else {
            jtaOutputConsole.setText("Compilación terminada...");
        }
        jtaOutputConsole.setCaretPosition(0);
    }

    private void lexicalAnalysis() {
        // Extraer tokens
        Lexer lexer;
        try {
            File codigo = new File("code.encrypter");
            FileOutputStream output = new FileOutputStream(codigo);
            byte[] bytesText = vista.getJtpCode().getText().getBytes();
            output.write(bytesText);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(new FileInputStream(codigo), "UTF8"));
            lexer = new Lexer(entrada);
            while (true) {
                Token token = lexer.yylex();
                if (token == null) {
                    break;
                }
                tokens.add(token);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("El archivo no pudo ser encontrado... " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Error al escribir en el archivo... " + ex.getMessage());
        }
    }

    private void semanticAnalysis() {
        if (valores_identificadores == null) {
            System.err.println("Error: valores_identificadores no está inicializado");
            return;
        }
        cleanErrorsTable();
        cleantriplos();
        Boolean  byfor= false;
        int contGlobal=0;
        int back=0;
        tokeninfo=objObt.Dotxt(tokens);
        objObt.guardarResultadosEnTxt(tokeninfo, "archivo");
        
        for (int i = 0; i < tokens.size(); i++) {
            contGlobal=getfilas()+1;
            Token token = tokens.get(i);
            String lexeme = token.getLexeme();
            if (lexeme.equals("for")) {
                int contfor = 0;
                int line = token.getLine();
                int line1 = token.getLine();
                boolean isthere = false;
                
                int next=i+1;
                byfor=true;
                do {
                    for (int y = i; y < tokens.size(); y++) {
                        token = tokens.get(y);
                        lexeme = token.getLexeme();
                        line1 = token.getLine();
                        if (lexeme.matches(".*\\}.*")) {
                            isthere = true;
                            back=y;
                            break;  
                        }
                    }

                    
                } while (!isthere);
                fillTableDatos(objFor.analyzeForLoop(i, tokens, line,line1,contGlobal));
                // i=back;
                
                i=next;
                continue;
            }
            if (byfor) {
                
                byfor=(i==back)?false:true;
            }
            if (lexeme.matches("[a-zA-Z][a-zA-Z0-9]*")) {
                String nextToken = getNextTokenValue(i);
                if (nextToken.equals("=")) {
                    int line = token.getLine();
                    String errorLexeme = lexeme;
                    String expressionType = evaluateExpression(i + 2, line, lexeme);
                    if (byfor==false) {
                        fillTableDatos(obj.datos(i + 2, line, lexeme, tokens,contGlobal));
                    }
                   
                   
                    allIdentifiers.add(lexeme);
                    
                    if (valores_identificadores.containsKey(lexeme)) {
                        String existingType = valores_identificadores.get(lexeme);
                        if (!existingType.equals(expressionType)) {
                            if (existingType.equals("")) {
                                valores_identificadores.put(lexeme, expressionType);
                            } else {
                                fillTableErrors(line, "Incompatibilidad de tipos: " + lexeme, OriginError);
                                continue;
                            }
                        }
                    } else {
                        valores_identificadores.put(lexeme, expressionType);
                    }
                    
                    if (expressionType.equals("ERROR")) {
                        System.out.println("Error en expresión: " + expressionType);
                    }
                    identificadores.put(lexeme, expressionType);
                }
            }
        }
        
        for (Map.Entry<String, String> entry : valores_identificadores.entrySet()) {
            System.out.println("Clave: " + entry.getKey() + ", Valor: " + entry.getValue());
        }
        fillTableTokensWithTypes();
    }
    private void cleanErrorsTable() {
        N_error=0;
        DefaultTableModel model = (DefaultTableModel) vista.getT_errors().getModel();
        model.setRowCount(0);
    }


    private String getNextTokenValue(int index) {
        if (index + 1 < tokens.size()) {
            return tokens.get(index + 1).getLexeme();
        }
        return "";
    }

    private void fillTableTokensWithTypes() {
        Set<String> processedLexemes = new HashSet<>();
        DefaultTableModel model = (DefaultTableModel) vista.getT_lexemas().getModel();
        model.setRowCount(0);  // Limpiar la tabla
    
        for (Token token : tokens) {
            String lexeme = token.getLexeme();
           
            if (!processedLexemes.contains(lexeme)) {
                processedLexemes.add(lexeme);
                System.out.println(lexeme);
                String type = identificadores.getOrDefault(lexeme, token.getLexicalComp());
                model.addRow(new Object[]{lexeme, type});
            }
        }
    }

   
    private void fillTableErrors(int line, String errorType, String error) {
        Object[] row = new Object[4];
        N_error++;
        DefaultTableModel model = (DefaultTableModel) vista.getT_errors().getModel();
        if (errorType.startsWith("Incompatibilidad de tipos:")) {
            row[1]=error;
        }
        else{
            row[1] = Errorlexeme; 
        }
        row[0] = "error" + N_error;
            // Lexema del error
        row[2] = line;       // Línea del error
        row[3] = errorType; // Descripción del error
        
        model.addRow(row);
    }
    //UNA VEZ ASIGANO EL TIPO DEL IDENTIFICADOR NO SE DEBE MODIFICAR
    //ESTA TOMANDO VALORES DE VARIABLES QUE AUN NO EXISTEN  
    private String evaluateExpression(int startIndex, int line, String Identificador) {
        StringBuilder expression = new StringBuilder();
        Token token;
        String lexeme = "";
        boolean hasString = false;
        boolean hasNumber = false;
        boolean isConcatenation = false;
        boolean isFirstToken = true;
        boolean isInvalidOperation = false;
        boolean hasChar = false;
        boolean hasReal=false;
        String lastType = "";
        
        for (int i = startIndex; i < tokens.size(); i++) {
            token = tokens.get(i);
            lexeme = token.getLexeme();
            String lexicalComp = token.getLexicalComp();
            if (token.getLine() == line) {
                if (i == startIndex) {
                    OriginError = lexeme;
                }
                
                if (lexeme.equals("=")) {
                    if (!isFirstToken) {
                        break; // Fin de la expresión cuando se encuentra el '='
                    }
                }
                
                // Verificar si es CADENA o CHAR
                if (lexicalComp.equals("CADENA")) {
                    hasString = true;
                    lastType = "CADENA";
                } else if (lexicalComp.equals("CHAR")) {
                    hasChar = true;
                    lastType = "CHAR";
                } else if (lexicalComp.equals("ENTERO") || lexicalComp.equals("REAL")) {

                    hasNumber = true;
                    lastType = "NUMERO";
                } else if (lexeme.equals("+")) {
                    isConcatenation = true;
                } else if (lexeme.equals("*") || lexeme.equals("/") || lexeme.equals("-")||lexeme.equals("%")) {
                    // Solo permitimos operaciones aritméticas para ENTERO y REAL
                    if (lastType.equals("CADENA") || lastType.equals("CHAR")) {
                        isInvalidOperation = true;
                    }
                } else if (lexicalComp.equals("IDENTIFICADOR")) {
                    
                    Errorlexeme = lexeme;
                    String idType = valores_identificadores.get(lexeme);
                    if (idType != null) {
                        if (idType.equals("CADENA")) {
                            hasString = true;
                        } else if (idType.equals("CHAR")) {
                            hasChar = true;
                        } else if (idType.equals("ENTERO") || idType.equals("REAL")) {
                            if (idType.equals("REAL")) {
                                hasReal=true;
                            }
                            hasNumber = true;
                        }
                        lastType = idType;
                    } else {
                        fillTableErrors(line, "Variable indefinida", null);
                        return "";
                    }
                }
                if ((hasString || hasChar) && (hasNumber)) {
                    isInvalidOperation = true;
                }

                expression.append(lexeme);
                isFirstToken = false;
            }
        }
        if (isInvalidOperation) {             
            fillTableErrors(line, "Incompatibilidad de tipos: " + Identificador, OriginError);             
            return "";          
        } else if (hasString) {             
            return isConcatenation ? "CADENA" : "CADENA";         
        } else if (hasChar) {             
            return isConcatenation ? "CADENA" : "CHAR";         
        } else if (hasNumber) {             
            boolean hasDecimalPoint = expression.toString().contains(".");             
            boolean hasFraction = expression.toString().matches(".*[eE][+-]?\\d+.*");  // Ajuste de la expresión regular para fracciones científicas
                    
            if (hasDecimalPoint || hasFraction||hasReal) {                 
                return "REAL";             
            } else {                 
                return "ENTERO";             
            }         
        } else {             
            fillTableErrors(line, "Variable indefinida", null);             
            return "";         
        }
        
    }
    private void removeUndefinedIdentifiers() {
        DefaultTableModel model = (DefaultTableModel) vista.getT_lexemas().getModel();
    
        ArrayList<Integer> rowsToRemove = new ArrayList<>();
        
        for (int i = 0; i < model.getRowCount(); i++) {
            String tipo = (String) model.getValueAt(i, 1); 
            
            if ("IDENTIFICADOR".equals(tipo)) {
                String identificador = (String) model.getValueAt(i, 0); 
                
                if (!identificadores.containsKey(identificador)) {
                    rowsToRemove.add(i);
                }
            }
        }
        
       
        for (int i = rowsToRemove.size() - 1; i >= 0; i--) {
            int rowIndex = rowsToRemove.get(i);
            model.removeRow(rowIndex);
        }
    }
    private void fillTableDatos(ArrayList<Object[]> rows) {
        DefaultTableModel model = (DefaultTableModel) vista.getTable_1().getModel();
        for (Object[] row : rows) {
            model.addRow(row);  // Añade cada fila3 del ArrayList a la tabla
        }
    }
    private void cleantriplos() {
        obj.init();
        N_error=0;
        DefaultTableModel model = (DefaultTableModel) vista.getTable_1().getModel();
        model.setRowCount(0);
    }
    public int getfilas(){
        DefaultTableModel model = (DefaultTableModel) vista.getTable_1().getModel();
        model.getRowCount();
        return model.getRowCount();
    }
}
