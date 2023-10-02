package lol.skript.addondocdata.syntax;

import org.jetbrains.annotations.Nullable;

public record DocSyntaxInfo (
    SyntaxType type,
    String addon,
    String className,
    String name,
    String[] description,
    String[] examples,
    @Nullable String since,
    @Nullable String[] requirements,
    @Nullable String[] events,
    @Nullable String[] typeUsage
) { }
