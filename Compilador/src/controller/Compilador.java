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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import controller.*;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JFrame;

import View.view;


public class Compilador {

    private view vista; // Vista que el compilador va a controlar
    private Directory directorio;
    private ArrayList<Token> tokens;
    private ArrayList<ErrorLSSL> errors;
    private ArrayList<TextColor> textsColor;
    private int N_error;
    private ArrayList<Production> identProd;
    private HashMap<String, String> identificadores;
    private HashMap<String, String> valores_identificadores;
    private boolean codeHasBeenCompiled = false;
    private javax.swing.JTextArea jtaOutputConsole;
    private  String Errorlexeme;
    private Set<String> vistosValor1;
    private HashSet<String> allIdentifiers = new HashSet<>();


    // Constructor
    public Compilador(view vista) {
        this.vista = vista;
        init();
    }
    public Compilador(){

    }

    private void init() {
        jtaOutputConsole = new javax.swing.JTextArea();
        tokens = new ArrayList<>();
        errors = new ArrayList<>();
        textsColor = new ArrayList<>();
        identProd = new ArrayList<>();
        identificadores = new HashMap<>();
        valores_identificadores = new HashMap<>();
        vistosValor1 = new HashSet<>();
        allIdentifiers = new HashSet<>();

        N_error=0;
        Errorlexeme="";
        
    }

    public void nuevoArchivo() {
        vista.getDirectory().New();
        clearFields();
    }

    public void abrirArchivo() {
        if (vista.getDirectory().Open()) {
            clearFields();
            // Color analysis u otra lógica después de abrir el archivo
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
        Functions.clearDataInTable(tblTokens);
        jtaOutputConsole.setText("");
        tokens.clear();
        errors.clear();
        identProd.clear();
        identificadores.clear();
        vistosValor1.clear();
        codeHasBeenCompiled = false;
    }
    private void compile() {

        clearFields();
        lexicalAnalysis();
        fillTableTokens();
        syntacticAnalysis();
        semanticAnalysis();
        printConsole();
        codeHasBeenCompiled = true;
        removeUndefinedIdentifiers();
        
    }
    private void fillTableTokens() {
        tokens.forEach(token -> {
           String Valor1= token.getLexeme();
           if (!vistosValor1.add(Valor1)) { // Si add() devuelve false, el valor ya estaba en el set
        }
           else{
            System.out.println("Token: " + Valor1 + ", Tipo: " + token.getLexicalComp());
            Object[] data = new Object[]{token.getLexeme(), token.getLexicalComp()};
            Functions.addRowDataInTable(vista.getT_lexemas(), data);
           }
        });
    }

     private void syntacticAnalysis() {
        Grammar gramatica = new Grammar(tokens, errors);
        gramatica.show();
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
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            String lexeme = token.getLexeme();
    
            if (lexeme.matches("[a-zA-Z][a-zA-Z0-9]*")) {  // Identificador
                String nextToken = getNextTokenValue(i);
                if (nextToken.equals("=")) {
                    int line= token.getLine();
                    Errorlexeme=lexeme;
                    String expressionType = evaluateExpression(i + 2, line);  // +2 para saltar el identificador y '='
                    allIdentifiers.add(lexeme);
                    valores_identificadores.put(lexeme,expressionType);
                    if (expressionType.equals("ERROR")) {
                        System.out.println("Error en expresión: " + expressionType);
                    }
                    identificadores.put(lexeme, expressionType);
                }
            }
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
                String type = identificadores.getOrDefault(lexeme, token.getLexicalComp());
                model.addRow(new Object[]{lexeme, type});
            }
        }
    }


    private void fillTableErrors(int line, String errorType) {
        DefaultTableModel model = (DefaultTableModel) vista.getT_errors().getModel();
        
        Object[] row = new Object[4];
        N_error++;
        row[0] = "error" + N_error;
        row[1] = Errorlexeme;     // Lexema del error
        row[2] = line;       // Línea del error
        row[3] = errorType; // Descripción del error
        
        model.addRow(row);
    }
    private String evaluateExpression(int startIndex, int line) {
        StringBuilder expression = new StringBuilder();
        String OriginError = "";
        Token token;
        String lexeme = "";
        boolean hasString = false;
        boolean hasNumber = false;
        boolean isConcatenation = false;
        boolean isFirstToken = true;
        boolean isInvalidOperation = false;
    
        // Variable para guardar el último tipo de token que hemos procesado
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
                System.out.println(lexeme);
                System.out.println(lexicalComp);
                if (lexicalComp.equals("CADENA")) {
                    hasString = true;
                    lastType = "CADENA";
                } else if (lexicalComp.equals("ENTERO") || lexicalComp.equals("REAL")) {
                    hasNumber = true;
                    lastType = "NUMERO";
                } else if (lexeme.equals("+")) {
                    isConcatenation = true;
                } else if (lexeme.equals("*") || lexeme.equals("/") || lexeme.equals("-")) {
                    // Verifica la operación de multiplicación o división
                    if (lastType.equals("CADENA")) {
                        isInvalidOperation = true; // Operación inválida si el último tipo fue cadena
                    }
                } else if (lexicalComp.equals("IDENTIFICADOR")) {
                    Errorlexeme=lexeme;
                    String idType = valores_identificadores.get(lexeme);
                    if (idType != null) {
                        if (idType.equals("CADENA")) {
                            hasString = true;
                        } else if (idType.equals("ENTERO") || idType.equals("REAL")) {
                            hasNumber = true;
                        }
                        lastType = idType;
                    }
                    else{
                        fillTableErrors(line, "Variable indefinida");
                        return "";
                    }
                }
    
                expression.append(lexeme);
    
                // Marca que ya no estamos en el primer token
                isFirstToken = false;
            }
        }
    
        // Evaluar el tipo basado en las características de la expresión
        if (isInvalidOperation) {
            fillTableErrors(line, "Incompatibilidad de tipos: " + OriginError);
            return ""; // Marcar como error si se encontró una operación inválida
        } else if (hasString) {
            return isConcatenation ? "CADENA" : "CADENA";
        } else if (hasNumber) {
            
            boolean hasDecimalPoint = expression.toString().contains(".");
            boolean hasFraction = expression.toString().matches(".*[eE][+-]?\\d+.*");
            System.out.println(lexeme);
            System.out.println(expression);
            if (hasDecimalPoint || hasFraction) {
                System.out.println(lexeme);
                return "REAL";
            } else {
                return "ENTERO";
            }
        } else {
            fillTableErrors(line, "Variable indefinida");
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
}
