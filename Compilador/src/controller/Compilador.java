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
    private ArrayList<Production> identProd;
    private HashMap<String, String> identificadores;
    private HashMap<String, String> valores_identificadores;
    private boolean codeHasBeenCompiled = false;
    private javax.swing.JTextArea jtaOutputConsole;
    private HashSet<String> detectedTokens;
    private  String valor1;
    private Set<String> vistosValor1;

    // Constructor
    public Compilador(view vista) {
        this.vista = vista;
        init();
    }
    public Compilador(){

    }

    private void init() {
        detectedTokens = new HashSet<>();
        jtaOutputConsole = new javax.swing.JTextArea();
        tokens = new ArrayList<>();
        errors = new ArrayList<>();
        textsColor = new ArrayList<>();
        identProd = new ArrayList<>();
        identificadores = new HashMap<>();
        vistosValor1 = new HashSet<>();
        
    }
public HashMap identificadores(){
    return identificadores;
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

    public void ejecutarCodigo(){
            compilarCodigo();
        if (codeHasBeenCompiled) {
            if (!errors.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No se puede ejecutar el código ya que se encontró uno o más errores",
                        "Error en la compilación", JOptionPane.ERROR_MESSAGE);
            } else {
                CodeBlock codeBlock = Functions.splitCodeInCodeBlocks(tokens, "{", "}", ";");
                System.out.println(codeBlock);
                ArrayList<String> blocksOfCode = codeBlock.getBlocksOfCodeInOrderOfExec();
                System.out.println(blocksOfCode);

            }
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
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            String lexeme = token.getLexeme();
    
            if (lexeme.matches("[a-zA-Z][a-zA-Z0-9]*")) {  // Identificador
                String nextToken = getNextTokenValue(i);
    
                if (nextToken.equals("=")) {
                    String expressionType = evaluateExpression(i + 2);  // +2 para saltar el identificador y '='
                    if (expressionType.equals("ERROR")) {
                        System.out.println("Error en expresión: " + expressionType);
                    }
                    identificadores.put(lexeme, expressionType);
                }
            }
        }
        fillTableTokensWithTypes();
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
    
    private void updateIdentifierTypes() {
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            String lexeme = token.getLexeme();
            
            if (lexeme.matches("[a-zA-Z][a-zA-Z0-9]*")) {  // Identificador
                String nextToken = getNextTokenValue(i);
                
                if (nextToken.equals("=")) {
                    String expressionType = evaluateExpression(i + 2);  // +2 para saltar el identificador y '='
                    if (!expressionType.equals("DESCONOCIDO")) {
                        identificadores.put(lexeme, expressionType);
                    }
                }
            }
        }
    }
    
    private String evaluateExpression(int startIndex) {
        StringBuilder expression = new StringBuilder();
        boolean hasString = false;
        boolean hasNumber = false;
        boolean isConcatenation = false;
        boolean isFirstToken = true;
    
        for (int i = startIndex; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            String lexeme = token.getLexeme();
            String lexicalComp = token.getLexicalComp();
    
            // Ignorar tokens que no sean parte de la expresión actual
            if (lexeme.equals(";")) {
                break;  // Fin de la expresión
            }
    
            if (lexeme.equals("=")) {
                if (!isFirstToken) {
                    break; // Fin de la expresión cuando se encuentra el '='
                }
            }
    
            if (lexicalComp.equals("CADENA")) {
                hasString = true;
            } else if (lexicalComp.equals("ENTERO") || lexicalComp.equals("REAL")) {
                hasNumber = true;
            } else if (lexeme.equals("+")) {
                isConcatenation = true;
            } else if (lexicalComp.equals("IDENTIFICADOR")) {
                
                String idType = identificadores.get(lexeme);
                
                if (idType != null) {
                    if (idType.equals("CADENA")) {
                        hasString = true;
                    } else if (idType.equals("ENTERO") || idType.equals("REAL")) {
                        hasNumber = true;
                    }
                }
            }
    
            expression.append(lexeme);
    
            // Marca que ya no estamos en el primer token
            isFirstToken = false;
        }
    
        // Evaluar el tipo basado en las características de la expresión
        if (hasString) {
            return isConcatenation ? "CADENA" : "DESCONOCIDO";
        } else if (hasNumber) {
            boolean hasDecimalPoint = expression.toString().contains(".");
            boolean hasFraction = expression.toString().matches(".*[eE][+-]?\\d+.*");
    
            if (hasDecimalPoint || hasFraction) {
                return "REAL";
            } else {
                return "ENTERO";
            }
        } else {
            return "DESCONOCIDO";
        }
    }
    
    
}
