### ScriptASM

<img src="https://github.com/Ecdcaeb/ScriptASM/blob/main/icon.png" alt="image" width="100" height="auto">

a script mod enables you write asm codes in scripts.

Check [Wiki](https://github.com/Ecdcaeb/ScriptASM/wiki) for details.

#### example :

```java
#compiler javaScript
#import org.apache.logging.log4j.LogManager
#import org.apache.logging.log4j.Logger

System.out.print("Script Strat his own tests!!! ^o^");
final Logger LOGGER = LogManager.getLogger("Cute script");
LOGGER.info("Now, i have my own logger!!");

// Change the title
TransformerRegistry.registerASMExplicitTransformer(-99 , 
 (cn) -> {
		for (var mn :cn.methods) {
			var itr = mn.instructions.iterator();
			while (itr.hasNext()) {
				if (itr.next() instanceof LdcInsnNode ldc && ldc.cst instanceof String str && str.startsWith("Cleanroom Loader")) {
					ldc.cst = str + " & Cute Script";
					LOGGER.info("change the title to {}", ldc.cst);
				}
			}
		}
	}, "net.minecraft.client.Minecraft");
```

![image](https://github.com/user-attachments/assets/e89ec1cc-7cea-483d-a204-3c6d545466e1)


