package net.heyzeer0.aladdin.manager.utilities;

import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.heyzeer0.aladdin.profiles.utilities.Paginator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by HeyZeer0 on 12/06/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class PaginatorManager {

    public static HashMap<String, Paginator> paginators = new HashMap<>();
    private static ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    public static void registerPaginator(Paginator pg) {
        paginators.put(pg.getActualId(), pg);
    }

    public static void updatePaginator(MessageReactionAddEvent e) {
        if(paginators.containsKey(e.getMessageId())) {
            Paginator pg = paginators.get(e.getMessageId());
            paginators.remove(e.getMessageId());
            pg.changePage(e);
            paginators.put(pg.getActualId(), pg);
        }
    }

    public static void startCleanup() {
        service.scheduleAtFixedRate(() -> {
            if(paginators.size() >= 1) {
                List<String> to_delete = new ArrayList<>();
                for(String ks : paginators.keySet()) {
                    if(paginators.get(ks).clear()) {
                        to_delete.add(ks);
                    }
                }
                to_delete.forEach(k -> paginators.remove(k));
            }
        }, 0, 15, TimeUnit.SECONDS);
    }

}
