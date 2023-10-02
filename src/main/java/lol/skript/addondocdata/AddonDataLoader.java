package lol.skript.addondocdata;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.*;
import ch.njol.skript.lang.*;
import lol.skript.addondocdata.syntax.DocSyntaxInfo;
import lol.skript.addondocdata.syntax.SyntaxType;
import org.bukkit.plugin.Plugin;
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
    }

    public boolean isInAddon(SyntaxElementInfo<? extends SyntaxElement> element) {
        return element.getElementClass().getPackageName().startsWith(this.addonPackage);
    }

//    private void loadEvents() {
//        List<SkriptEventInfo<?>> events = Skript.getEvents().stream().filter(this::isInAddon).toList();
//        for (SkriptEventInfo<?> event : events) {
//            syntaxes.add(new DocSyntaxInfo(
//                    SyntaxType.EVENT, addonName,
//                    event.getName(), event.getDescription(), event.getExamples(),
//                    event.getSince(), event.getRequiredPlugins(), event.getKeywords(),
//                    null, null
//            ));
//        }
//    }
//
//    private void loadConditions() {
//        List<SyntaxElementInfo<? extends Condition>> conditions = Skript.getConditions().stream().filter(this::isInAddon).toList();
//        for (SyntaxElementInfo<? extends Condition> condition : conditions) {
//            syntaxes.add(new DocSyntaxInfo(
//                    SyntaxType.EVENT, addonName,
//                    condition.getName(), condition.getDescription(), condition.getExamples(),
//                    condition.getSince(), condition.getRequiredPlugins(), condition.getKeywords(),
//                    null, null
//            ));
//        }
//    }

    // annotated elements are: structures, expressions, effects, conditions, sections

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
            if (!expr.c.getPackageName().startsWith(addonPackage)) continue;
            if (expr.c.getAnnotation(NoDoc.class) != null) continue;
            loadAnnotatedSingle(SyntaxType.EXPRESSION, expr);
        }

        // Effects
        for (SyntaxElementInfo<? extends Effect> effect : Skript.getEffects()) {
            if (!effect.c.getPackageName().startsWith(addonPackage)) continue;
            if (effect.c.getAnnotation(NoDoc.class) != null) continue;
            loadAnnotatedSingle(SyntaxType.EFFECT, effect);
        }

        // Conditions
        for (SyntaxElementInfo<? extends Condition> condition : Skript.getConditions()) {
            if (!condition.c.getPackageName().startsWith(addonPackage)) continue;
            if (condition.c.getAnnotation(NoDoc.class) != null) continue;
            loadAnnotatedSingle(SyntaxType.CONDITION, condition);
        }

        // Sections
        for (SyntaxElementInfo<? extends Section> section : Skript.getSections()) {
            if (!section.c.getPackageName().startsWith(addonPackage)) continue;
            if (section.c.getAnnotation(NoDoc.class) != null) continue;
            loadAnnotatedSingle(SyntaxType.SECTION, section);
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
