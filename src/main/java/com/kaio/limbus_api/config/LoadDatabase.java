package com.kaio.limbus_api.config;

import com.kaio.limbus_api.entity.*;
import com.kaio.limbus_api.enums.PassivaType;
import com.kaio.limbus_api.enums.Rarity;
import com.kaio.limbus_api.enums.Resistance;
import com.kaio.limbus_api.enums.Sin;
import com.kaio.limbus_api.enums.SkillSlot;
import com.kaio.limbus_api.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


// Popula o banco H2 (que é em memória e começa vazio a cada execução) com dados iniciais.
//  Para ligar: app.seed.enabled=true no application.properties.

//  ATENÇÃO: os valores de Skills, Passivas, Stats e Sanity da Identity de exemplo são ILUSTRATIVOS.

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class LoadDatabase implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    private final SinnerRepository sinnerRepository;
    private final TagRepository tagRepository;
    private final IdentityRepository identityRepository;
    private final IdentityStatsRepository statsRepository;
    private final SanityRepository sanityRepository;
    private final SkillRepository skillRepository;
    private final PassiveRepository passiveRepository;

    public LoadDatabase(SinnerRepository sinnerRepository,
                        TagRepository tagRepository,
                        IdentityRepository identityRepository,
                        IdentityStatsRepository statsRepository,
                        SanityRepository sanityRepository,
                        SkillRepository skillRepository,
                        PassiveRepository passiveRepository) {
        this.sinnerRepository = sinnerRepository;
        this.tagRepository = tagRepository;
        this.identityRepository = identityRepository;
        this.statsRepository = statsRepository;
        this.sanityRepository = sanityRepository;
        this.skillRepository = skillRepository;
        this.passiveRepository = passiveRepository;
    }

    @Override
    public void run(String... args) {
        if (sinnerRepository.count() > 0) {
            return;
        }

        List<String> nomes = List.of("Yi Sang", "Faust", "Don Quixote", "Ryōshū", "Meursault", "Hong Lu",
                "Heathcliff", "Ishmael", "Rodion", "Sinclair", "Outis", "Gregor");
        List<Sinner> sinners = nomes.stream().map(Sinner::new).map(sinnerRepository::save).toList();
        Sinner rodion = sinners.get(8);

        Tag houseOfSpiders = tagRepository.save(new Tag("The House of Spiders"));
        tagRepository.save(new Tag("The Fingers"));
        tagRepository.save(new Tag("The Index"));
        tagRepository.save(new Tag("The Thumb"));

        Identity identity = new Identity("[The House of Spiders: The Thumb Nursefather] Rodion", 4, Rarity.ZERO_ZERO_ZERO, rodion);
        Set<Tag> tags = new HashSet<>();
        tags.add(houseOfSpiders);
        identity.setTags(tags);
        identity = identityRepository.save(identity);

        statsRepository.save(new IdentityStats(248, "4-7", 65, 149, Resistance.INEFFECTIVE, Resistance.NORMAL, Resistance.FATAL, identity));

        sanityRepository.save(new Sanity(
                ". Panic Effects Does not act for this turn",
                "Increases after winning a Clash based on the Clash count (Base Value is 10, raised by 20% per clash after 1) . Increase by 10 after this unit defeats an enemy whose level was higher than or equal to unit's . increse by 5 after an ally defeats an enemy whose level was higher than or equal to the unit's",
                "· If the level of the defeated ally was higher than or equal to unit's, decrease based on the level difference (Base Value is 10, raised by 10 per level)",
                identity));

        skillRepository.save(new Skill(SkillSlot.SKILL_1, 1, Sin.PRIDE, "Colpi di Taglio", 2, "skill 1", identity));
        skillRepository.save(new Skill(SkillSlot.SKILL_2, 1, Sin.SLOTH, "I'll Gladly Blast a Hole Through Ya", 3, "skill 2", identity));
        skillRepository.save(new Skill(SkillSlot.SKILL_3, 1, Sin.WRATH, "Sezionatura di Elefante", 4, "skill 3", identity));
        skillRepository.save(new Skill(SkillSlot.DEFESA, 1, Sin.PRIDE, "Fuck Off!", 2, "defense skill", identity));

        passiveRepository.save(new Passive(PassivaType.BATTLE, "The Eye of Precognition", "battle passive 1.", identity));
        passiveRepository.save(new Passive(PassivaType.SUPPORT, "Weaklings, Not Worth Shit", "support passive", identity));

        log.info("Banco populado: {} Sinners, 3 Tags e 1 Identity completa (id {}).", sinners.size(), identity.getId());
    }
}
