# Vanilla Injector

Et lett Dependency Injection-rammeverk skrevet i plain Java, som gjenskaper kjernemekanismene fra Spring sin IoC-container (`ApplicationContext`, `@Autowired`, `@Component`, `@Configuration`/`@Bean`) fra bunnen av.

## Hvorfor dette prosjektet finnes

Plain Java har ingen innebygd dependency injection-mekanisme. Rammeverk som Spring, Guice og Dagger løser dette, men bruken av dem skjuler *hvordan* det faktisk fungerer under panseret.

Dette prosjektet er bygget for å demonstrere forståelse av mekanismene disse rammeverkene bygger på — reflection, classpath-skanning, avhengighetsgrafer og rekursiv objektkonstruksjon — ved å implementere en minimal, men fungerende, versjon selv.

## Funksjonalitet

- **Komponentskanning** — finner alle klasser merket `@Component` på et gitt classpath
- **Constructor injection** — løser og injiserer avhengigheter automatisk via konstruktør
- **Singleton-scope** — hver bean konstrueres kun én gang og gjenbrukes
- **Sirkulær avhengighet-deteksjon** — feiler tydelig i stedet for `StackOverflowError`
- **Interface-til-implementasjon-mapping** — `getBean(PaymentService.class)` finner riktig implementasjon
- **`@Configuration` + `@Bean`** — factory-metoder for objekter som ikke kan skannes (lambda/SAM-implementasjoner, tredjepartsklasser du ikke eier)

## Arkitektur

```
ApplicationContext   ← offentlig API, brukes fra "composition root"
      │
      ├── ClasspathScanner   → finner @Component- og @Configuration-klasser
      │
      └── BeanFactory        → løser avhengighetsgrafen rekursivt
                │
                └── BeanDefinition  → metadata: konstruktør/factory-metode + avhengigheter
```

Flyten ved oppstart:

1. `ApplicationContext` skanner det angitte pakkenavnet
2. For hver funnet klasse bygges en `BeanDefinition` (konstruktør eller `@Bean`-metode, pluss avhengighetenes typer)
3. Når `getBean(Type)` kalles, løser `BeanFactory` avhengighetene rekursivt og konstruerer objektet med `Constructor.newInstance()` eller `Method.invoke()`

## Kom i gang

### Forutsetninger

- Java 17+
- Maven

### Avhengigheter

```xml
<dependency>
    <groupId>org.reflections</groupId>
    <artifactId>reflections</artifactId>
    <version>0.10.2</version>
</dependency>
```

## Eksempel på bruk

```java
public interface PaymentService {
    void process(double amount);
}

@Component
public class StripePaymentService implements PaymentService {
    public void process(double amount) {
        System.out.println("Behandler betaling: " + amount);
    }
}

@Component
public class OrderService {
    private final PaymentService paymentService;

    @Autowired
    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void placeOrder(double total) {
        paymentService.process(total);
    }
}

public class Main {
    public static void main(String[] args) {
        var context = ApplicationContext.run("no.dittapp");
        var orderService = context.getBean(OrderService.class);
        orderService.placeOrder(499.0);
    }
}
```

For objekter du ikke eier klassen til (tredjepartsbiblioteker, lambda-implementasjoner):

```java
@Configuration
public class HttpClientConfig {
    @Bean
    public OkHttpClient httpClient() {
        return new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();
    }
}
```

## Prosjektstruktur

Prosjektet er delt i to Maven-moduler — selve biblioteket og en eksempelapplikasjon som bruker det. Dette tvinger et tydelig skille mellom offentlig API og intern implementasjon:

```
VanilaInjector-parent/
├── pom.xml                          ← parent-pom, binder modulene sammen
├── VanilaInjector-core/                ← selve biblioteket
│   └── src/main/java/org/dev/
│       ├── annotations/              ← offentlig API
│       │   ├── Component.java
│       │   ├── Autowired.java
│       │   ├── Configuration.java
│       │   └── Bean.java
│       ├── core/
│       │   ├── ApplicationContext.java   ← offentlig API
│       │   ├── BeanDefinition.java       ← pakke-privat
│       │   ├── BeanFactory.java          ← pakke-privat
│       │   └── ClasspathScanner.java     ← pakke-privat
```

`BeanFactory`, `BeanDefinition` og `ClasspathScanner` er bevisst ikke `public` — all bruk av biblioteket skal gå gjennom `ApplicationContext`, den ene tiltenkte inngangsporten.

## Bruke biblioteket i eget prosjekt

Installer lokalt til `.m2`-cachen:

```bash
cd mini-spring-core
mvn clean install
```

## Begrensninger og videre arbeid

- `@Qualifier` for å skille mellom flere implementasjoner av samme interface er ikke implementert ennå
- Kun singleton-scope støttes (ingen prototype-scope)
- Ingen støtte for `@PostConstruct`-lignende livssyklus-hooks
- Ingen AOP-støtte (f.eks. `@Transactional`)

## Bakgrunn

Bygget som et porteføljeprosjekt for å demonstrere dyp forståelse av dependency injection-mekanismer, med bakgrunn fra fire år med backend-utvikling i Java, Kotlin og Spring Boot.
