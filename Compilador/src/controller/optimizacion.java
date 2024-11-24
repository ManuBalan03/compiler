package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import compilerTools.Token;

public class optimizacion {
    private ArrayList<Token> tokens;

    public optimizacion() {}

    public ArrayList<TokenInfo> Dotxt(ArrayList<Token> tokens) {
        this.tokens = tokens;

        // Crear los mapas necesarios
        HashMap<String, Integer> identificadoresFrecuencia = new HashMap<>();
        HashMap<String, List<Integer>> identificadoresLineas = new HashMap<>();
        HashMap<String, Integer> identificadoresSeguidosDeIgual = new HashMap<>();
        HashMap<String, Integer> identificadoresEnFor = new HashMap<>();
        HashSet<Integer> lineasAEliminar = new HashSet<>();

        // Contar frecuencias y registrar líneas donde el identificador aparece seguido de un "="
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            String lexeme = token.getLexeme().trim();
            String type = token.getLexicalComp();
            int line = token.getLine();

            if (type.equals("IDENTIFICADOR")) {
                // Incrementar frecuencia
                identificadoresFrecuencia.put(lexeme, identificadoresFrecuencia.getOrDefault(lexeme, 0) + 1);

                // Verificar si está seguido de un "="
                if (getNextTokenValue(i).equals("=")) {
                    identificadoresLineas
                        .computeIfAbsent(lexeme, k -> new ArrayList<>())
                        .add(line);
                    identificadoresSeguidosDeIgual.put(lexeme, line);
                }

                // Verificar si el identificador está en un bucle for (simplificación)
                if (lexeme.equals("for")) {
                    identificadoresEnFor.put(lexeme, line);
                }
            }
        }

        // Verificar identificadores que cumplen los requisitos
        for (String identificador : identificadoresFrecuencia.keySet()) {
            List<Integer> lineas = identificadoresLineas.get(identificador);
            int frecuencia = identificadoresFrecuencia.get(identificador);

            // Verificar que:
            // 1. Frecuencia sea mayor a 2
            // 2. El identificador solo aparezca en líneas con "="
            // 3. Todas las líneas tengan tamaño 3
            if (frecuencia > 1 
                && lineas != null 
                && lineas.size() == frecuencia 
                && todasLasLineasCumplen(lineas)) {
                for (int linea : lineas) {
                    lineasAEliminar.add(linea);
                }
            }
        }

        // Identificar líneas a eliminar
        for (String identificador : identificadoresFrecuencia.keySet()) {
            int frecuencia = identificadoresFrecuencia.get(identificador);
            Integer linea = identificadoresSeguidosDeIgual.get(identificador);

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
        
       
        return  reasignarLineasConsecutivas(tokensResultantes);
    }

    private boolean todasLasLineasCumplen(List<Integer> lineas) {
        for (int linea : lineas) {
            if (calcularTamanioDeLinea(tokens, linea) != 3) {
                return false;
            }
        }
        return true;
    }

    private String getNextTokenValue(int index) {
        if (index + 1 < tokens.size()) {
            return tokens.get(index + 1).getLexeme();
        }
        return "";
    }

    private int calcularTamanioDeLinea(ArrayList<Token> tokens, int linea) {
        int count = 0;
        for (Token token : tokens) {
            if (token.getLine() == linea) {
                count++;
            }
        }
        return count;
    }

    
    public static void guardarResultadosEnTxt(List<TokenInfo> tokens, String nombreArchivo) {
        // El archivo se creará en la ruta del proyecto, puedes cambiar la ruta si lo deseas
        File archivo = new File(nombreArchivo);
    
        // Usamos BufferedWriter para escribir en el archivo
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            // Crear un mapa donde las claves sean los números de línea y los valores listas de lexemas
            Map<Integer, StringBuilder> lineTokensMap = new HashMap<>();
    
            // Agrupar los lexemas por línea
            for (TokenInfo token : tokens) {
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
    public ArrayList<TokenInfo> reasignarLineasConsecutivas(ArrayList<Token> tokensResultantes) {
        ArrayList<TokenInfo> tokensReasignados = new ArrayList<>();
        int nuevaLinea = 1; // Nueva línea consecutiva
        int nuevaColumna = 1; // Contador para las columnas en cada línea
        int lineaActualOriginal = tokensResultantes.get(0).getLine(); // Línea original de referencia
    
        for (Token token : tokensResultantes) {
            // Si el token pertenece a una nueva línea original
            if (token.getLine() != lineaActualOriginal) {
                nuevaLinea++; // Avanzar a la siguiente línea consecutiva
                
                lineaActualOriginal = token.getLine(); // Actualizar la línea original de referencia
            }
    
            // Crear un nuevo TokenInfo con las líneas y columnas reasignadas
            TokenInfo nuevoTokenInfo = new TokenInfo(
                token.getLexeme(),      // Lexema original
                token.getLexicalComp(), // Componente léxico original
                nuevaLinea,             // Nueva línea consecutiva
                nuevaColumna            // Columna en la nueva línea
            );
    
            tokensReasignados.add(nuevoTokenInfo);
            nuevaColumna++; // Incrementar la columna para el siguiente token en la misma línea
        }
    
        // Imprimir tokens reasignados para verificar el resultado
        for (TokenInfo token : tokensReasignados) {
            System.out.println("-----------");
            System.out.println("Lexema: " + token.getLexeme());
            System.out.println("Línea: " + token.getLine());
            System.out.println("Columna: " + token.getColumn());
            System.out.println("-----------");
        }
    
        return tokensReasignados;
    }
    
}
