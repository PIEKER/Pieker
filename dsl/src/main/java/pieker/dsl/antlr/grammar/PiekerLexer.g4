/*
 * The MIT License
 *
 * Copyright 2022 Karate Labs Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
lexer grammar PiekerLexer;

FEATURE_COMMENT: WSLF* '#' CHAR* NEWLINE -> channel(HIDDEN) ;
FEATURE: WSLF* 'Feature:' WS* -> pushMode(MAIN) ; // we never popMode !

fragment WSLF: [\r\n \t] ;     // White Space or Line Feed
fragment BOL: [\r\n]+ [ \t]* ; // Beginning Of Line
fragment WS: [ \t] ;           // White Space

mode MAIN; // ==================================================================


BACKGROUND: 'Background:' WS* ;

SCENARIO: 'Scenario:' WS* ;
BEFORE_EACH: 'BeforeEach:' WS* ;
STEP: 'Step:' WS* ;

// General Keyword
DEF: '@def' WS+ ;

// Architecture Keyword
GIVEN: 'Given:' ;
LINK: '@link' WS+;
REQUEST: '@request' WS+ ;
SQL: '@sql' WS+ ;
PASSIVE: '@passive' WS+;
SERVICE: '@service' WS+ ;
URL: '@url' WS+ ;
DATABASE: '@database' WS+;

// Condition Keyword
WHEN: 'When:' ;
AFTER: '@after' WS+ ;
RETRY: '@retry' WS+ ;
TIMES: '@times' WS+ ;
DELAY: '@delay' WS+ ;
DROPOUT: '@dropout' WS+ ;
TIMEOUT: '@timeout' WS+;

// Test Keyword
THEN: 'Then:' ;
LOG_ALL: 'LogAll:' WS+;
ASSERT: 'Assert:';
ASSERT_AFTER: 'After:';
DATABASE_BLOCK: 'Database:';
TRAFFIC_BLOCK: 'Traffic:';

IDENTIFIER: 'Identifier:' WS+;
TABLE: 'Table:' WS+;
BOOL: 'Bool:' WS+;
EQUALS: 'Equals:' WS+;
NULL: 'Null:' WS+;

STATUS: '@status' WS+ ;
TIME: '@time' WS+ ;
AMOUNT: '@amount' WS+ ;
CONTENT: '@content' WS+ ;

COMMENT: NEWLINE '#' CHAR* -> channel(HIDDEN) ;
TABLE_ROW: NEWLINE '|' CHAR+ ;
DOC_STRING: NEWLINE '"""' .*? '"""' CHAR* NEWLINE;

CHAR: ~[\r\n] ;
NEWLINE: BOL+ ;
