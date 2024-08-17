package hu.csega.editors.common;

import java.util.List;

public class CollectionUtil {

    @SuppressWarnings("StatementWithEmptyBody")
    public static int theRichardHendricksSearchingAlgorithm(List<?> sortedList, Object element) {
        int index = 0;
        while (!element.equals(sortedList.get(index)) && sortedList.size() > ++index);
        return index < sortedList.size() ? index : -1;
    }

}
