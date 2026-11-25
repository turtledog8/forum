INSERT INTO users (first_name, last_name, email, username, password, role, blocked) VALUES
    ('Georgi', 'Ivanov',   'georgi@forum.local', 'georgi', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'USER', FALSE),
    ('Maria',  'Dimitrova','maria@forum.local',  'maria',  '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'USER', FALSE),
    ('Boris',  'Kolev',    'boris@forum.local',  'boris',  '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'USER', TRUE);

INSERT INTO tags (name) VALUES
    ('cpu'),
    ('memory'),
    ('psu'),
    ('overclocking');

INSERT INTO posts (user_id, title, content, created_at) VALUES
    ((SELECT id FROM users WHERE username = 'ivan'),
     'RTX 4070 Super vs RX 7800 XT for 1440p in 2026',
     'Been running both cards back to back on the same 7800X3D test bench. The 4070 Super pulls ahead in anything with ray tracing thanks to DLSS 3 frame generation, and it does it at noticeably lower power draw, usually 200-215W under full load versus 260W+ for the 7800 XT. The 7800 XT claws it back in raw rasterization at 1440p and has 16GB of VRAM against 12GB, which matters more now that some texture packs are creeping past 10GB. If you play competitive shooters at high refresh, get the 4070 Super for the lower latency and frame gen. If you are running a mixed library and want headroom for the next couple of years, the extra VRAM on the 7800 XT is the safer bet.',
     CURRENT_TIMESTAMP),
    ((SELECT id FROM users WHERE username = 'elena'),
     'Why I finally switched from a 12700K to a 7800X3D',
     'The 12700K was still fast, but the idle power draw and the space heater effect under load pushed me to AM5. Single CCD, no scheduler quirks, and the 3D V-Cache makes an obvious difference in anything CPU bound. Baldur''s Gate 3 in the city went from dipping into the 70s to holding a steady 120+, and CS2 1% lows improved even more than the averages did. Temps cap out around 89C by design which looks alarming on a graph but is completely normal for this chip, it just runs the cache hot. Total platform power at the wall dropped by close to 40W compared to the old rig doing the same benchmark loop.',
     CURRENT_TIMESTAMP),
    ((SELECT id FROM users WHERE username = 'georgi'),
     'Air versus AIO cooling after three separate builds',
     'Third build in a row where I went back to a Noctua NH-D15 instead of a 240 or 360mm AIO, and I do not regret it once. The Arctic Liquid Freezer II I ran for two years developed a faint pump whine after about 18 months that got worse over time, and I have heard the same story from three other people. The NH-D15 is louder to install because it barely clears some RAM heatsinks, but once it is in there is nothing to fail except a fan. Performance difference on a 65W or 105W part is close to a rounding error. I would only go AIO now if the case genuinely cannot fit a 165mm tower cooler, or you are running something that actually needs the extra headroom like a 14900K under load.',
     CURRENT_TIMESTAMP),
    ((SELECT id FROM users WHERE username = 'maria'),
     'Is 32GB of RAM actually necessary for gaming now',
     'Ran a week of normal usage with Discord, a dozen browser tabs, and OBS open in the background while gaming, and 16GB was tight but usable, 32GB never went above 60% used. The games themselves mostly sit in the 10-14GB range now, it is everything else running alongside them that pushes you over. If you stream, record, or run a browser with a hundred tabs like some of you clearly do, get 32GB, a kit like Corsair Vengeance 6000 CL30 in dual channel is not expensive anymore. If you close everything before you play and only game, 16GB dual channel is still genuinely fine and the price gap is better spent on the GPU.',
     CURRENT_TIMESTAMP),
    ((SELECT id FROM users WHERE username = 'ivan'),
     'My 800 dollar 1080p high refresh build for a friend',
     'Parts list for anyone chasing high refresh 1080p on a tight budget: Ryzen 5 7600, RTX 4060, 16GB DDR5 6000 CL30, a 1TB Gen4 NVMe drive, a B650 board with just enough VRM for the 7600 not to throttle, and a 650W 80+ Bronze unit with some headroom for a future GPU upgrade. Skipped the X3D chips entirely at this budget since the 7600 is not the bottleneck here, the 4060 is. In Valorant we are comfortably over 300fps, CS2 sits around 250-280 depending on the map, and Apex holds mid 100s on a mix of high and medium settings. Nothing exotic, just balanced parts that do not bottleneck each other.',
     CURRENT_TIMESTAMP),
    ((SELECT id FROM users WHERE username = 'elena'),
     'TrueNAS Scale vs Unraid after a year running both',
     'Ran TrueNAS Scale on one box and Unraid on another for a full year to actually compare them instead of just reading opinions. TrueNAS with ZFS is the better pure storage system, snapshots and replication are rock solid and scrubbing catches bit rot before it becomes a problem, but it wants ECC RAM and does not love you adding drives of mismatched sizes to a pool. Unraid''s array lets you mix and match drive sizes freely and the Community Apps plugin ecosystem makes spinning up new services trivial, but it is not true RAID so per-drive read speed is your ceiling on single-stream transfers. Idle power draw was basically identical on the same hardware, around 38W at the wall for both. Pure NAS with snapshots that matter, TrueNAS. Home server doing NAS plus a pile of Docker containers and VMs, Unraid.',
     CURRENT_TIMESTAMP),
    ((SELECT id FROM users WHERE username = 'georgi'),
     'PSU wattage calculators overestimate more than you think',
     'Hooked a Kill A Watt up to three different builds and compared actual draw against what the popular online PSU calculators recommended. Every single one padded the number by 150-250W over what the system ever pulled, even during a Furmark plus Prime95 combined stress test. The real risk is not average draw, it is transient spikes on high-end GPUs, a 4090 can spike well past its rated board power for a few milliseconds and that is what trips a marginal unit, not the sustained load. My rule now: measure or estimate real draw, add 20-30% headroom, and spend the difference on a genuinely good 80+ Gold or Platinum unit like a Corsair RM850x or Seasonic Focus rather than buying more raw wattage than you will ever use from a budget brand.',
     CURRENT_TIMESTAMP),
    ((SELECT id FROM users WHERE username = 'maria'),
     'Undervolting your GPU is free performance nobody discusses',
     'Spent an evening with the curve editor in MSI Afterburner on a 4070 Ti and it was worth it. Stock the card was boosting to about 2700MHz at 1.05V, after curve editing I got the same clocks at 0.9V, and pushed it slightly further to 2775MHz at 0.925V once temps dropped enough to leave headroom. Ran back to back Superposition and a two hour Cyberpunk session to confirm stability before trusting it day to day. Result was the same or slightly better performance, fan curve barely spins up now, and the card runs about 8C cooler under load. Costs nothing, takes twenty minutes, and every GPU I have tried it on has had some slack in the stock voltage curve.',
     CURRENT_TIMESTAMP);

INSERT INTO post_tags (post_id, tag_id)
SELECT p.id, t.id FROM posts p, tags t WHERE
    (p.title = 'RTX 4070 Super vs RX 7800 XT for 1440p in 2026' AND t.name IN ('gpu'))
    OR (p.title = 'Why I finally switched from a 12700K to a 7800X3D' AND t.name IN ('cpu'))
    OR (p.title = 'Air versus AIO cooling after three separate builds' AND t.name IN ('cooling'))
    OR (p.title = 'Is 32GB of RAM actually necessary for gaming now' AND t.name IN ('memory'))
    OR (p.title = 'My 800 dollar 1080p high refresh build for a friend' AND t.name IN ('budget-build', 'gpu', 'cpu'))
    OR (p.title = 'TrueNAS Scale vs Unraid after a year running both' AND t.name IN ('homelab', 'nas'))
    OR (p.title = 'PSU wattage calculators overestimate more than you think' AND t.name IN ('psu'))
    OR (p.title = 'Undervolting your GPU is free performance nobody discusses' AND t.name IN ('gpu', 'overclocking'));

INSERT INTO comments (post_id, user_id, content, created_at)
SELECT p.id, u.id, c.content, CURRENT_TIMESTAMP FROM posts p, users u,
    (SELECT 'RTX 4070 Super vs RX 7800 XT for 1440p in 2026' AS title, 'elena' AS username,
            'Went with the 7800 XT for exactly the VRAM reason you mentioned, glad to see it validated by someone else''s numbers.' AS content
     UNION ALL
     SELECT 'RTX 4070 Super vs RX 7800 XT for 1440p in 2026', 'maria',
            'How much of the ray tracing gap is frame gen specifically versus raw RT cores? Curious if it holds up without DLSS 3.'
     UNION ALL
     SELECT 'Why I finally switched from a 12700K to a 7800X3D', 'georgi',
            'The power draw difference alone would justify it for me, my room noticeably warms up less now.'
     UNION ALL
     SELECT 'Why I finally switched from a 12700K to a 7800X3D', 'ivan',
            '89C sounds scary but yeah that is just how the V-Cache chips are tuned, mine sits there too under all-core load.'
     UNION ALL
     SELECT 'Air versus AIO cooling after three separate builds', 'maria',
            'Had the exact same pump whine issue on a two year old AIO, ended up replacing the whole unit under warranty.'
     UNION ALL
     SELECT 'Is 32GB of RAM actually necessary for gaming now', 'ivan',
            'Recommended 32GB to a friend who streams and it fixed their stuttering immediately, background load really is the deciding factor.'
     UNION ALL
     SELECT 'My 800 dollar 1080p high refresh build for a friend', 'elena',
            'Solid list, would you swap anything if the budget went up by 100?'
     UNION ALL
     SELECT 'TrueNAS Scale vs Unraid after a year running both', 'georgi',
            'This matches my experience, moved from Unraid to TrueNAS once snapshots became something I actually relied on.'
     UNION ALL
     SELECT 'TrueNAS Scale vs Unraid after a year running both', 'maria',
            'What does your Docker setup look like on the TrueNAS side, still using the built in apps catalog?'
     UNION ALL
     SELECT 'PSU wattage calculators overestimate more than you think', 'elena',
            'Never thought about transient spikes specifically, that explains a PSU shutdown I had that made no sense at the time.'
     UNION ALL
     SELECT 'Undervolting your GPU is free performance nobody discusses', 'ivan',
            'Did this on my 4070 last weekend after reading this, temps dropped a solid 7C, thank you.'
     UNION ALL
     SELECT 'Undervolting your GPU is free performance nobody discusses', 'georgi',
            'Worth adding for anyone trying this: stress test longer than you think you need to, instability can show up an hour in.'
    ) c
WHERE p.title = c.title AND u.username = c.username;

INSERT INTO post_likes (post_id, user_id)
SELECT p.id, u.id FROM posts p, users u WHERE
    (p.title = 'RTX 4070 Super vs RX 7800 XT for 1440p in 2026' AND u.username IN ('elena', 'maria', 'georgi'))
    OR (p.title = 'Why I finally switched from a 12700K to a 7800X3D' AND u.username IN ('ivan', 'georgi'))
    OR (p.title = 'Air versus AIO cooling after three separate builds' AND u.username IN ('maria', 'elena', 'ivan'))
    OR (p.title = 'Is 32GB of RAM actually necessary for gaming now' AND u.username IN ('ivan'))
    OR (p.title = 'My 800 dollar 1080p high refresh build for a friend' AND u.username IN ('elena', 'maria'))
    OR (p.title = 'TrueNAS Scale vs Unraid after a year running both' AND u.username IN ('georgi', 'maria', 'ivan'))
    OR (p.title = 'PSU wattage calculators overestimate more than you think' AND u.username IN ('elena', 'ivan', 'maria'))
    OR (p.title = 'Undervolting your GPU is free performance nobody discusses' AND u.username IN ('ivan', 'georgi', 'elena'));
