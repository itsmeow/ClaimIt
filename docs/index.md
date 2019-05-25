# ClaimIt API Documentation
ClaimIt API has been designed for modders to integrate their mods and develop addons, so I will be using my best efforts to document how to use it.
<br>

## Getting started

How does one actually set up development for ClaimIt? How should I go about integrating my mod?<br>
The first choice is whether to include ClaimIt API as an *optional* dependency in your base mod, or to develop an external/addon mod.<br>

The benefits of an addon mod:

   * Can reference ClaimIt API directly, no need for proxies or lambda hacks to avoid classloading
   * Can post events in your base mod to check for ClaimIt hooks in addon mod<br>

The benefits of integration:

   * No need to maintain another mod
   * Keeps everything contained
   * Less likelihood of everything breaking if your mod changes

### Adding it to the project

Once you've figured out which one you want to do, you'll need to add ClaimIt API to your buildscript.<br>
This is fairly simple. First, add the maven to your repositories.<br>
By default all Forge build scripts already include jcenter, but if needed in your `build.gradle`:

```
repositories {
	jcenter()
}
```

Now, you need to add the dependency on ClaimIt API. Inside the `dependencies` block, put this:

```
dependencies {
	deobfCompile 'its_meow.claimit:claimitapi:1.12.2-latest.version.here'
}
```

Make sure you replace the version with the latest, so as not to be incompatible. Keep it up to date if you can.<br>
Now that you've done this, rerun `gradlew setupDecompWorkspace` and your IDE task `gradlew eclipse` if applicable. You should also refresh Eclipse (F5 on project).<br>
You should now see ClaimIt API in your build path!

## How To: Optional Dependencies

You may skip this if you are developing an addon mod. This is a quick how-to for those who wish to optionally depend on ClaimIt API in their mod.<br>
First, I introduce the concept of classloading. A class is loaded if it is referenced in code, as an argument in a function, as a return type, or as a field. If a class is loaded that does not exist, the game will crash. So, you must avoid classloading ClaimIt API should it not be present.<br>
Luckily, Forge includes the `Loader` class, which you can use to check if a mod is loaded. Using this, you can execute code only if a mod is loaded.

```java
boolean apiPresent = Loader.isModLoaded("claimitapi");
```

### The Reflect Way

However, just an if statement is not enough to stop classloading! Remember, I said references in code load classes. So, how do you avoid this?
The first way is via reflection. This is generally not recommended, but it is probably a little easier for those who don't understand lambdas.
Here's an example of using it to return a class that extends a common proxy interface, which you can call methods on without classloading should something not be present:

```
    /** Get the compatability proxy for a given modid, uses reflection.
     * @param modid - Modid to check if loaded
     * @param classNameActive - The class name to return if the mod is active
     * @param classNameInactive - The class name to return if the mod is not present
     * @return The proper proxy class for whether the mod is loaded or not **/
    private static <T>T getInteropProxy(Class<T> type, String modid, String classNameActive, String classNameInactive) {
        T proxy = null;
        try {
            if (Loader.isModLoaded(modid)) {
                // reflection to avoid hard dependency
                proxy = Class.forName(classNameActive).asSubclass(type).newInstance();
            } else {
                proxy = Class.forName(classNameInactive).asSubclass(type).newInstance();
            }
        } catch(ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            logger.error("Error retrieving compatibility class. This is a bug.");
        }
        return proxy;
}
```

With this method, say you had an active compatability class in `net.yourmod.compat.ActiveClaimIt` and a corresponding `net.yourmod.compat.InactiveClaimIt` which both extend `ClaimItCompatProxy`<br>
`ClaimItCompatProxy` has an abstract method called `register()`, which when called will do something that classloads ClaimIt inside the Active class, while the Inactive class does nothing.
So, should I want to do an optional dependency call during preinit, I could do something like this:

```java
@EventHandler
public void preInit(FMLPreinitializationEvent event) {
	ClaimItCompatProxy proxy = <ClaimItCompatProxy>getInteropProxy(ClaimItCompatProxy.class, "claimitapi", "net.yourmod.compat.ActiveClaimIt", "net.yourmod.compat.InactiveClaimIt");
	if(proxy != null) {
		proxy.register();
	}
}
```

Now, with this, you have sucessfully avoided a classload! You can even optionally *statically* subscribe event bus events inside your `register()` method (or equivalent) like so:

```java
public void register() {
	MinecraftForge.EVENT_BUS.register(ActiveClaimIt.class);
}

@SubscribeEvent
public static void onSomethingChangeInMod(YourEventHere event) {
	// Do something that depends on both an event an API
}
```

Now, you can post your own events that get passed to your ClaimIt API module, should it exist, without classloading!

### The Lambda Way

I did mention another method, this is lambdas. This one is very simple should you know how to utilize it. There's a small trick in Java that if you have *TWO* lambda operators in a row and then classload, simply having it as a variable somewhere doesn't classload the innermost lambda.
For example, I can safely do the following:

```java
Supplier<Runnable> run = () -> () -> {
	// Insert all sorts of references to ClaimIt API here
};

if(Loader.isLoaded("claimitapi") {
	run.get().run();
}
```

Now that you have the rundown of avoiding classloading, you can safely develop using ClaimIt API directly in your mod! Addon makers are still however free to do whatever they please, with a hard dependency.

## What next?

There are more specific pages for pieces of the API that you could wish to interact with. Check them out!
