package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import compilerTools.Token;

public class optimizacion {
    private ArrayList<Token> tokens;

    public optimizacion() {
    }

    public ArrayList<Token> Dotxt(ArrayList<Token> tokens) {
        this.tokens = tokens;

        // Crear los mapas
        HashMap<String, Integer> identificadoresFrecuencia = new HashMap<>();
        HashMap<String, Integer> identificadoresLineas = new HashMap<>();
        HashMap<String, Boolean> identificadoresEnFor = new HashMap<>();
        HashMap<String, Integer> identificadoresSeguidosDeIgual = new HashMap<>();

        ArrayList<Integer> lineasAEliminar = new ArrayList<>();
        boolean dentroDeFor = false;

        // Primera pasada: identificar variables en bucles for
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            String lexeme = token.getLexeme().trim();
            String type = token.getLexicalComp();

            if (lexeme.equals("for")) {
                dentroDeFor = true;
            } else if (lexeme.equals("}")) {
                dentroDeFor = false;
            }

            if (dentroDeFor && type.equals("IDENTIFICADOR")) {
                identificadoresEnFor.put(lexeme, true);
            }
        }

        // Segunda pasada: contar frecuencias y analizar líneas
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            String lexeme = token.getLexeme().trim();
            String type = token.getLexicalComp();
            int line = token.getLine();

            if (type.equals("IDENTIFICADOR")) {
                identificadoresFrecuencia.put(lexeme, identificadoresFrecuencia.getOrDefault(lexeme, 0) + 1);
                identificadoresLineas.putIfAbsent(lexeme, line);

                String nextTokenValue = getNextTokenValue(i);
                if (nextTokenValue != null && nextTokenValue.trim().equals("=")) {
                    identificadoresSeguidosDeIgual.put(lexeme, line);
                }
            }
        }

        // Identificar líneas a eliminar
        for (String identificador : identificadoresFrecuencia.keySet()) {
            int frecuencia = identificadoresFrecuencia.get(identificador);
            int linea = identificadoresLineas.get(identificador);

            // No eliminar si está en un bucle for o es usado más de una vez
            if (frecuencia == 1 && 
                identificadoresSeguidosDeIgual.containsKey(identificador) && 
                !identificadoresEnFor.containsKey(identificador)) {
                lineasAEliminar.add(linea);
            }
        }

        // Eliminar las líneas marcadas
        ArrayList<Token> tokensResultantes = new ArrayList<>();
        for (Token token : tokens) {
            if (!lineasAEliminar.contains(token.getLine())) {
                tokensResultantes.add(token);
            }
        }

        // Guardar los resultados en el archivo

        // Limpiar los HashMap
        identificadoresFrecuencia.clear();
        identificadoresLineas.clear();
        identificadoresEnFor.clear();
        identificadoresSeguidosDeIgual.clear();

        return tokensResultantes;
    }

    private String getNextTokenValue(int index) {
        if (index + 1 < tokens.size()) {
            return tokens.get(index + 1).getLexeme();
        }
        return "";
    }

    private HashMap<Integer, Integer> calcularTamanioLineas(ArrayList<Token> tokens) {
        HashMap<Integer, Integer> lineasTamanio = new HashMap<>();
        for (Token token : tokens) {
            int line = token.getLine();
            lineasTamanio.put(line, lineasTamanio.getOrDefault(line, 0) + 1);
        }
        return lineasTamanio;
    }

    public static void guardarResultadosEnTxt(List<Token> tokens, String nombreArchivo) {
        // El archivo se creará en la ruta del proyecto, puedes cambiar la ruta si lo deseas
        File archivo = new File(nombreArchivo);
    
        // Usamos BufferedWriter para escribir en el archivo
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            // Crear un mapa donde las claves sean los números de línea y los valores listas de lexemas
            Map<Integer, StringBuilder> lineTokensMap = new HashMap<>();
    
            // Agrupar los lexemas por línea
            for (Token token : tokens) {
                int line = token.getLine();  // Obtener la línea del token
                lineTokensMap
                    .computeIfAbsent(line, k -> new StringBuilder()) // Si la línea no existe, crear un nuevo StringBuilder
                    .append(token.getLexeme()).append(" "); // Añadir el lexema a la línea correspondiente, separado por un espacio
            }
    
            // Escribir los lexemas agrupados por línea en el archivo, ordenados por número de línea
            writer.write("CODIGO OPTIMIZADO: \n");
            writer.write("-------------------------------\n");
    
            // Ordenar las claves (números de línea) en orden ascendente
            lineTokensMap.keySet().stream()
                    .sorted() // Ordenar las claves de menor a mayor
                    .forEach(line -> {
                        try {
                            writer.write(lineTokensMap.get(line).toString().trim() + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
    
    
            System.out.println("Los resultados se han guardado correctamente en " + nombreArchivo);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al guardar el archivo: " + e.getMessage());
        }
    }
}
