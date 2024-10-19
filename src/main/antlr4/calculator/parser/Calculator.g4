lexer grammar Calculator;

fragment DIGIT: [0-9];

OPEN_BRACKET: '(';
CLOSED_BRACKET: ')';

CARET: '^';
STAR: '*';
SLASH: '/';
PLUS: '+';
MINUS: '-';

NUMBER: ('-')? DIGIT+ ('.' DIGIT+)?;

SPACES: [ \u000B\t\r\n\p{White_Space}] -> skip;
COMMENT: '/*' .*? '*/' -> skip;
LINE_COMMENT: '//' ~[\r\n]* -> skip;