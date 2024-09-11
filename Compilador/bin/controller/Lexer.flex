import compilerTools.Token;
import java.util.HashSet;

%%
%class Lexer
%type Token
%line
%column
%{
    private Token token(String lexeme, String lexicalComp, int line, int column){
        return new Token(lexeme, lexicalComp, line+1, column+1);
    }
%}
/* Variables básicas de comentarios y espacios */
TerminadorDeLinea = \r|\n|\r\n
EntradaDeCaracter = [^\r\n]
EspacioEnBlanco = {TerminadorDeLinea} | [ \t\f]
ComentarioTradicional = "/*" [^*] ~"*/" | "/*" "*"+ "/"
FinDeLineaComentario = "//" {EntradaDeCaracter}* {TerminadorDeLinea}?
ContenidoComentario = ( [^*] | \*+ [^/*] )*
ComentarioDeDocumentacion = "/**" {ContenidoComentario} "*"+ "/"

/* Comentario */
Comentario = {ComentarioTradicional} | {FinDeLineaComentario} | {ComentarioDeDocumentacion}

/* Identificador */
Letra = [A-Za-zÑñ_ÁÉÍÓÚáéíóúÜü]
Digito = [0-9]
Identificador = {Letra}({Letra}|{Digito})*

/* Números */
Entero = 0 | [1-9][0-9]*
Flotante = {Entero}"."{Digito}+([eE][+-]?{Digito}+)?

/* Operadores */
Operadores = [\+\-\*/=;]

/* Cadenas de texto (Strings) */
Cadena = \"[^\"]*\"
%%

/* Comentarios o espacios en blanco */
{Comentario}|{EspacioEnBlanco} { /*Ignorar*/ }

/* Detectar operadores y no hacer nada */
{Operadores} { /* Ignorar operadores */ }

/* Detectar identificadores, sin duplicados */
{Identificador} { 
        System.out.println("Identificador detectado: " + yytext());
        return token(yytext(), "IDENTIFICADOR", yyline, yycolumn); 
    }
}

/* Detectar números flotantes, sin duplicados */
{Flotante} { 
    
        System.out.println("Número flotante detectado: " + yytext());
        return token(yytext(), "FLOTANTE", yyline, yycolumn); 
    }
}

/* Detectar números enteros, sin duplicados */
{Entero} { 
    
        System.out.println("Número entero detectado: " + yytext());
        return token(yytext(), "ENTERO", yyline, yycolumn); 
    
}

/* Detectar cadenas de texto (Strings) */
{Cadena} {
   
        System.out.println("Cadena detectada: " + yytext());
        return token(yytext(), "CADENA", yyline, yycolumn); 
    
}

/* Cualquier otra cosa será tratada como un error */
. { 
    if (!tokenExists(yytext())) {
        System.out.println("Error detectado: " + yytext());
        return token(yytext(), "ERROR", yyline, yycolumn); 
    }
}
