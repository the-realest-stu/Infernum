package infernum.common;

import infernum.Infernum;
import infernum.common.entities.InfernumEntities;
import infernum.common.items.InfernumItems;
import infernum.common.spells.InfernumSpells;
import infernum.common.world.WorldGeneratorInfernum;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import network.PacketHandler;

public class CommonProxy {
	
	public void preInit() {
		PacketHandler.registerMessages();
		InfernumSpells.init();
		InfernumItems.init();
		InfernumEntities.init();
		
		GameRegistry.registerWorldGenerator(new WorldGeneratorInfernum(), 0);
	}
	
	public void init() {
		
	}
	
	public void postInit() {
		
	}

}
