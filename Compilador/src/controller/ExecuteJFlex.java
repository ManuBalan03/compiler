package controller;


import jflex.exceptions.SilentExit;

/**
 *
 * 
 */
public class ExecuteJFlex {
    public static void main(String[] args) {
        String lexerFile = "C:/Users/halom/OneDrive/Documentos/GitHub/compiler/Compilador/src/controller/Lexer.flex";
        System.out.println("Ruta completa del archivo lexer: " + lexerFile);
        try {
            jflex.Main.generate(new String[]{lexerFile});
        } catch (SilentExit ex) {
            System.out.println("Error al compilar/generar el archivo flex: " + ex);
        }
    }
}