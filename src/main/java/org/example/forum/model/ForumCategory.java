package org.example.forum.model;

import java.util.Set;

public enum ForumCategory {

    CONSUMER_BUILDS("Consumer PC Building", Set.of("gpu", "cooling", "budget-build")),
    SERVERS_HOMELAB("Servers & Homelab", Set.of("homelab", "nas"));

    private final String displayName;
    private final Set<String> tags;

    ForumCategory(String displayName, Set<String> tags) {
        this.displayName = displayName;
        this.tags = tags;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Set<String> getTags() {
        return tags;
    }
}
