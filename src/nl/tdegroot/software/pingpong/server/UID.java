package nl.tdegroot.software.pingpong.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Timon on 6-1-14.
 */
public class UID {

    private static List<Integer> ids = new ArrayList<Integer>();
    private static final int RANGE = 10000;

    private static int index = 0;

    static {
        for (int i = 0; i < RANGE; i++) {
            ids.add(i);
        }
        Collections.shuffle(ids);
    }

    public static int getIdentifier() {
        if (index > ids.size() - 1) index = 0;
        return ids.get(index++);
    }

}
