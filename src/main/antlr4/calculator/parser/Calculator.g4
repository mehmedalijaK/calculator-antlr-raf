grammar Calculator;

start: statement* ;

statement
    : vectorDeclaration ';'
    | printStatement ';'
    | expr ';'
    ;

vectorDeclaration
    : 'vector' IDENTIFIER EQUAL vectorExpr  // Vector assignment
    | 'vector' IDENTIFIER EQUAL expr        // Vector can also be assigned
    ;

printStatement
    : PRINT OPEN_BRACKET printArg (COMMA printArg)* CLOSED_BRACKET // Print statement
    ;

printArg
    : IDENTIFIER                  // Print a single identifier
    | expr                        // Print an expression
    | vectorExpr                  // Print a vector expression
    ;

vectorExpr
    : VECTOR_OPEN (vectorElement (COMMA vectorElement)*)? VECTOR_CLOSE // 1D vector
    | VECTOR_OPEN (vectorExpr (COMMA vectorExpr)*)? VECTOR_CLOSE       // 2D vector
    ;

vectorElement
    : NUMBER
    | IDENTIFIER                    // Vector elements can be numbers or previously defined vectors
    | vectorExpr                    // Allow nested vector expressions
    ;

expr: additionExpr ((PLUS | MINUS) additionExpr)* ;

additionExpr
    : multiplicationExpr ((PLUS | MINUS) multiplicationExpr)*
    | vectorExpr ((PLUS | MINUS) vectorExpr)* ;

multiplicationExpr
    : exponentExpr ((STAR | SLASH) exponentExpr)* ;

exponentExpr
    : atom (CARET exponentExpr)? ;

atom
    : NUMBER
    | IDENTIFIER
    | OPEN_BRACKET expr CLOSED_BRACKET
    | vectorExpr
    ;

// Lexer rules
fragment DIGIT: [0-9];

OPEN_BRACKET: '(';
CLOSED_BRACKET: ')';

CARET: '^';
STAR: '*';
SLASH: '/';
PLUS: '+';
MINUS: '-';

VECTOR_OPEN: '<';
VECTOR_CLOSE: '>';
COMMA: ',';
EQUAL: '=';

PRINT: 'print';

NUMBER: ('-')? DIGIT+ ('.' DIGIT+)?; // Allows integers and decimals

IDENTIFIER: [a-zA-Z_] [a-zA-Z_0-9]*; // Identifiers for vectors (e.g., a, b, m)

SPACES: [ \u000B\t\r\n\p{White_Space}] -> skip; // Skips whitespace
COMMENT: '/*' .*? '*/' -> skip; // Skips block comments
LINE_COMMENT: '//' ~[\r\n]* -> skip; // Skips line comments