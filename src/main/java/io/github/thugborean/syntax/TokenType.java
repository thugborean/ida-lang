package io.github.thugborean.syntax;

public enum TokenType {
    // Literals
    NumericLiteral,
    DoubleLiteral,
    StringLiteral,
    CharacterLiteral,
    BooleanLiteral,
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
    Boolean, // bool

    // Functions and structures
    Function, // fn
    Void, // Used for returnType in functions
    Structure, // struct

    Print,

    // Loops and control flow
    If,
    Bang,
    EqualsEquals, // ==
    BangEquals, // !=
    LessThan, // <
    GreaterThan, // >
    LessThanOrEquals, // <=
    GreaterThanOrEquals, // >=
    While,
    For,
    Do,

    // Scope
    BracketOpen,
    BracketClosed,
    ParenthesesOpen,
    ParenthesesClosed,
    CurlyOpen,
    CurlyClosed,
    SemiColon,
    Dot, // .
    Comma, // ,

    // EOF
    EOF,
}