/* Данный класс свящывает условие (когда срабатывать) и действие (что делать)*/
package net.john_just.edans.skill;

import java.util.function.Consumer;
import java.util.function.Predicate;

// <E> - тип события (BreakEvent, ItemCraftedEvent и т.д.)
public class SkillTriggerEntry<E> {
    public final Predicate<E> condition; // Условие: true -> начисляем
    public final Consumer<E> action; // Само действие (начисление ОП)

    public SkillTriggerEntry(Predicate<E> condition, Consumer<E> action) {
        this.condition = condition;
        this.action = action;
    }
}
