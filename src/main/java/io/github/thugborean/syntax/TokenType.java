package io.github.thugborean.syntax;

public enum TokenType {
    // Literals
    NumericLiteral,
    DoubleLiteral,
    StringLiteral,
    CharLiteral,
    BoolLiteral,
    NullLiteral,
    True,
    False,

    // Identifier
    Identifier,

    // Operators
    Assign, // =
    Plus, // +
    Minus, // -
    Multiply, // *
    Divide, // /
    Modulo, // %

    DoubleQuote, // "
    SingleQuote, // '

    // Used on variables to assign their visibility
    Export, 
    Global, 
    Hidden,

    // Two-character operators
    PlusPlus, // ++
    MinusMinus, // --
    AtseriskAsterisk, // **

    Append, // +=
    Truncate, // -=

    Return, // ->

    // Variables
    Number, // num
    Double, // double
    String, // string
    Character, // char
    Bool, // bool

    // Functions and structures
    Function, // func
    Void, // Used for returnType in functions
    Structure, // struct

    Print,

    // Loops and control flow
    If,
    EqualsEquals, // ==
    NotEquals, // !=
    LessThan, // <
    GreaterThan, // >
    LessThanOrEquals, // <=
    GreaterThanOrEquals, // >=
    While,
    For,
    Do,

    // Scope
    OpenBracket,
    ClosedBracket,
    OpenParenthesis,
    CloseParenthesis,
    OpenCurly,
    ClosedCurly,
    SemiColon,
    Dot, // .

    // EOF
    EOF,
}