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
Flotante = {Entero}"."{Digito}+([eE][+-]?{Digito}+)? |

/* Operadores */
Operadores = [\+\-\*/=;<>] | "<+" | ">="

/* Palabras clave */
PalabraClave = "for"

/* Cadenas de texto (Strings) */
Cadena = \"[^\"]*\"
%%

/* Comentarios o espacios en blanco */
{Comentario}|{EspacioEnBlanco} { /* Ignorar */ }

/* Detectar operadores */
{Operadores} { 
    return token(yytext(), "OPERADOR", yyline, yycolumn); 
}

/* Detectar la palabra clave 'for' */
{PalabraClave} {
    return token(yytext(), "PALABRA_CLAVE", yyline, yycolumn);
}

/* Detectar identificadores */
{Identificador} { 
    return token(yytext(), "IDENTIFICADOR", yyline, yycolumn); 
}

/* Detectar números flotantes */
{Flotante} { 
    return token(yytext(), "FLOTANTE", yyline, yycolumn); 
}

/* Detectar números enteros */
{Entero} { 
    return token(yytext(), "ENTERO", yyline, yycolumn); 
}

/* Detectar cadenas de texto (Strings) */
{Cadena} {
    return token(yytext(), "CADENA", yyline, yycolumn); 
}

/* Cualquier otra cosa será tratada como un token vacío */
. { 
    return token("", "ERROR", yyline, yycolumn); 
}
