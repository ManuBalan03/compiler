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

import javax.swing.JOptionPane;
import javax.swing.JTable;
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
    private boolean codeHasBeenCompiled = false;
    private javax.swing.JTextArea jtaOutputConsole;
    

    // Constructor
    public Compilador(view vista) {
        this.vista = vista;
        init();
    }

    private void init() {
        jtaOutputConsole = new javax.swing.JTextArea();
        tokens = new ArrayList<>();
        errors = new ArrayList<>();
        textsColor = new ArrayList<>();
        identProd = new ArrayList<>();
        identificadores = new HashMap<>();
        
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
        codeHasBeenCompiled = false;
    }
    private void compile() {
        clearFields();
        // lexicalAnalysis();
        fillTableTokens();
        syntacticAnalysis();
        // semanticAnalysis();
        printConsole();
        codeHasBeenCompiled = true;
    }
    private void fillTableTokens() {
        tokens.forEach(token -> {
            Object[] data = new Object[]{token.getLexicalComp(), token.getLexeme()};
            Functions.addRowDataInTable(vista.getT_lexemas(), data);
        });
    }

     private void syntacticAnalysis() {
        Grammar gramatica = new Grammar(tokens, errors);
        gramatica.show();
        System.out.println("YOMARA ZZZ");
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

    // private void lexicalAnalysis() {
    //     // Extraer tokens
    //     Lexer lexer;
    //     try {
    //         File codigo = new File("code.encrypter");
    //         FileOutputStream output = new FileOutputStream(codigo);
    //         byte[] bytesText = jtpCode.getText().getBytes();
    //         output.write(bytesText);
    //         BufferedReader entrada = new BufferedReader(new InputStreamReader(new FileInputStream(codigo), "UTF8"));
    //         lexer = new Lexer(entrada);
    //         while (true) {
    //             Token token = lexer.yylex();
    //             if (token == null) {
    //                 break;
    //             }
    //             tokens.add(token);
    //         }
    //     } catch (FileNotFoundException ex) {
    //         System.out.println("El archivo no pudo ser encontrado... " + ex.getMessage());
    //     } catch (IOException ex) {
    //         System.out.println("Error al escribir en el archivo... " + ex.getMessage());
    //     }
    // }
}
