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
parser grammar PiekerParser ;

options { tokenVocab=PiekerLexer; }

feature: featureHeader background? scenario* NEWLINE? EOF ;

featureHeader: FEATURE (line NEWLINE?)? description? NEWLINE?;

background: BACKGROUND (description | NEWLINE) globalScope?;
globalScope: (DEF line NEWLINE)+;

scenario : SCENARIO line (description | NEWLINE) scenarioScope? beforeEach? step* ;
scenarioScope: (DEF line NEWLINE)+ ;

given: GIVEN (description | NEWLINE) givenCondition+;
givenCondition: givenKey line? NEWLINE?;
givenKey: PASSIVE? REQUEST | PASSIVE? SQL | URL | LINK | SERVICE | DATABASE;

when: WHEN (description | NEWLINE) whenCondition+;
whenCondition: whenKey line NEWLINE? ;
whenKey: AFTER | RETRY | TIMES| DELAY | DROPOUT | TIMEOUT | DEF ;

then: THEN (description | NEWLINE) logAll? assert* ;
logAll: LOG_ALL line NEWLINE? ;
assert: ASSERT line NEWLINE assertAfter? arguments? assertBody ;
assertAfter: ASSERT_AFTER line NEWLINE;
assertBody: (assertBool | assertEquals | assertNull)+ ;
arguments: ARGUMENTS line NEWLINE;
assertBool: boolHeader line NEWLINE?;
boolHeader: BOOL line NEWLINE ;
assertEquals: equalsHeader line NEWLINE?;
equalsHeader: EQUALS line NEWLINE ;
assertNull: nullHeader line NEWLINE?;
nullHeader: NULL line NEWLINE ;

description: DOC_STRING;

step: STEP line (description | NEWLINE) given? when? then? ;
beforeEach: BEFORE_EACH NEWLINE given when? then? ;

line: CHAR+ ;

table: TABLE_ROW+ ;