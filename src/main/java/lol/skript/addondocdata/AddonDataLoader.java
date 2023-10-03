package lol.skript.addondocdata;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.doc.*;
import ch.njol.skript.lang.*;
import ch.njol.skript.registrations.Classes;
import lol.skript.addondocdata.syntax.DocSyntaxInfo;
import lol.skript.addondocdata.syntax.SyntaxType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.structure.Structure;
import org.skriptlang.skript.lang.structure.StructureInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class AddonDataLoader {
    private String addonName;
    private Plugin addonPlugin;
    private String addonPackage;



    private List<DocSyntaxInfo> syntaxes = new ArrayList<>();
    public List<DocSyntaxInfo> getSyntaxes() {
        return syntaxes;
    }

    public AddonDataLoader(String addonName, Plugin addonPlugin) {
        this.addonName = addonName;
        this.addonPlugin = addonPlugin;
        this.addonPackage = addonPlugin.getClass().getPackageName();
        loadAnnotated();
        loadEvents();
        loadTypes();
    }

    public boolean isInAddon(Class<? extends SyntaxElement> c) {
        return isInAddon(c.getPackageName());
    }

    public boolean isInAddon(String packageName) {
        return packageName.startsWith(this.addonPackage);
    }

    private void loadAnnotated() {
        // Structures (TODO need to uncomment when 2.7 is standard!!)
//        for (StructureInfo<? extends Structure> struct : Skript.getStructures()) {
//            if (!struct.c.getPackageName().startsWith(addonPackage)) continue;
//            if (struct.c.getAnnotation(NoDoc.class) != null) continue;
//            loadAnnotatedSingle(SyntaxType.STRUCTURE, struct);
//        }

        // Expressions
        for (Iterator<ExpressionInfo<?, ?>> it = Skript.getExpressions(); it.hasNext(); ) {
            SyntaxElementInfo<? extends Expression> expr = it.next();
            if (!isInAddon(expr.c)) continue;
            if (expr.c.getAnnotation(NoDoc.class) != null) continue;
            loadAnnotatedSingle(SyntaxType.EXPRESSION, expr);
        }

        // Effects
        for (SyntaxElementInfo<? extends Effect> effect : Skript.getEffects()) {
            if (!isInAddon(effect.c)) continue;
            if (effect.c.getAnnotation(NoDoc.class) != null) continue;
            loadAnnotatedSingle(SyntaxType.EFFECT, effect);
        }

        // Conditions
        for (SyntaxElementInfo<? extends Condition> condition : Skript.getConditions()) {
            if (!isInAddon(condition.c)) continue;
            if (condition.c.getAnnotation(NoDoc.class) != null) continue;
            loadAnnotatedSingle(SyntaxType.CONDITION, condition);
        }

        // Sections
        for (SyntaxElementInfo<? extends Section> section : Skript.getSections()) {
            if (!isInAddon(section.c)) continue;
            if (section.c.getAnnotation(NoDoc.class) != null) continue;
            loadAnnotatedSingle(SyntaxType.SECTION, section);
        }
    }

    private void loadEvents() {
        for (SkriptEventInfo<?> event : Skript.getEvents()) {
            if (!isInAddon(event.c)) continue;
            if (event.c.getAnnotation(NoDoc.class) != null) continue;
            List<String> exceptions = new ArrayList<>();
            List<String> warns = new ArrayList<>();

            String name = event.getName();
            String[] description = event.getDescription();
            String[] examples = event.getExamples();
            String since = event.getSince();
            String[] requirements = event.getRequiredPlugins();

            if (description.length == 0) {
                exceptions.add("description");
            }
            if (examples.length == 0) {
                warns.add("examples");
            }
            if (since == null) {
                warns.add("since");
            }

            if (!exceptions.isEmpty()) {
                ADDMain.logger().severe("Event " + event.c.getSimpleName() + " was not loaded because it was missing: " + String.join(", ", exceptions));
                return;
            }
            if (!warns.isEmpty()) {
                ADDMain.logger().warning("Event " + event.c.getSimpleName() + " was loaded, but was missing helpful values: " + String.join(", ", warns));
            }

            DocSyntaxInfo syntax = new DocSyntaxInfo(SyntaxType.EVENT, this.addonName, event.c.getSimpleName(), name, description, examples, since, requirements, null, null);
            syntaxes.add(syntax);
        }
    }

    @SuppressWarnings("ConstantValue")
    private void loadTypes() {
        for (ClassInfo<?> type : Classes.getClassInfos()) {
            if (!isInAddon(type.getC().getPackageName())) continue;
            if (type.getC().getAnnotation(NoDoc.class) != null) continue;
            if (!type.hasDocs()) continue;
            List<String> exceptions = new ArrayList<>();
            List<String> warns = new ArrayList<>();

            String name = type.getDocName();
            String[] description = type.getDescription();
            String[] examples = type.getExamples();
            String since = type.getSince();
            String[] requirements = type.getRequiredPlugins();
            String[] typeUsage = type.getUsage();

            if (description == null || description.length == 0) {
                exceptions.add("description");
            }
            if (examples == null || examples.length == 0) {
                warns.add("examples");
            }
            if (since == null) {
                warns.add("since");
            }

            DocSyntaxInfo syntax = new DocSyntaxInfo(SyntaxType.TYPE, this.addonName, type.getC().getSimpleName(), name, description, examples, since, requirements, null, typeUsage);
            syntaxes.add(syntax);
        }
    }

    private void loadAnnotatedSingle(SyntaxType type, SyntaxElementInfo<?> element) {
        List<String> exceptions = new ArrayList<>();
        List<String> warns = new ArrayList<>();

        Name annoName = element.c.getAnnotation(Name.class);
        String name = null;
        if (annoName != null) {
            name = annoName.value();
        } else {
            exceptions.add("name");
        }

        Description annoDescription = element.c.getAnnotation(Description.class);
        String[] description = null;
        if (annoDescription != null) {
            description = annoDescription.value();
        } else {
            exceptions.add("description");
        }

        Examples annoExamples = element.c.getAnnotation(Examples.class);
        String[] examples = {};
        if (annoExamples != null) {
            examples = annoExamples.value();
        } else {
            warns.add("examples");
        }

        Since annoSince = element.c.getAnnotation(Since.class);
        String since = null;
        if (annoSince != null) {
            since = annoSince.value();
        } else {
            warns.add("since");
        }

        RequiredPlugins annoRequirements = element.c.getAnnotation(RequiredPlugins.class);
        String[] requirements = null;
        if (annoRequirements != null) {
            requirements = annoRequirements.value();
        }

        Events annoEvents = element.c.getAnnotation(Events.class);
        String[] events = null;
        if (type == SyntaxType.EXPRESSION && annoEvents != null) {
            events = annoEvents.value();
        }

        if (!exceptions.isEmpty()) {
            ADDMain.logger().severe("Element " + element.c.getSimpleName() + " was not loaded because it was missing: " + String.join(", ", exceptions));
            return;
        }
        if (!warns.isEmpty()) {
            ADDMain.logger().warning("Element " + element.c.getSimpleName() + " was loaded, but was missing helpful values: " + String.join(", ", warns));
        }

        DocSyntaxInfo syntax = new DocSyntaxInfo(type, this.addonName, element.c.getSimpleName(), name, description, examples, since, requirements, events, null);
        syntaxes.add(syntax);
    }
}
