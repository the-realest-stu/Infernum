package infernum.common.world;

import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import infernum.common.blocks.InfernumBlocks;
import infernum.common.blocks.TilePedestal;
import infernum.common.entities.EntityPigMage;
import infernum.common.items.InfernumItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;

public class InfernalMonumentGenerator extends WorldGenerator {
	
	public static final ResourceLocation MONUMENT_STRUCTURE = new ResourceLocation("infernum", "infernal_monument");

	private static int rarity = 50;
	
	@Override
	public boolean generate(World worldIn, Random rand, BlockPos posIn) {
		
		if (!(worldIn instanceof WorldServer)) {
			return false;
		}

		WorldServer world = (WorldServer) worldIn;

		if (world.provider.getDimension() != -1) {
			return false;
		}

		if (world.rand.nextInt(rarity) == 0) {
			int y = findPosY(world, posIn);
			if (y > 0) {
				BlockPos pos = new BlockPos(posIn.getX(), findPosY(world, posIn), posIn.getZ());
				generateMonument(world, pos, rand);
			}
			return true;
		}

		return false;

	}

	public void generateMonument(WorldServer world, BlockPos pos, Random rand) {
		MinecraftServer server = world.getMinecraftServer();
		Template template = world.getStructureTemplateManager().getTemplate(server, MONUMENT_STRUCTURE);
		PlacementSettings settings = new PlacementSettings();
		settings.setRotation(Rotation.values()[rand.nextInt(Rotation.values().length)]);
		
		BlockPos size = template.getSize();
		int airBlocks = 0;
		for(int x = 0; x < size.getX(); x++) {
			for (int z = 0; z < size.getZ(); z++) {
				if (world.isAirBlock(pos.add(template.transformedBlockPos(settings, new BlockPos(x, -1, z))))) {
					airBlocks++;
					if (airBlocks > 0.33 * (size.getX() * size.getZ())) {
						return;
					}
				}
			}
		}
		for (int x = 0; x < size.getX(); x++) {
			for (int z = 0; z < size.getZ(); z++) {
				if (x == 0 || x == size.getX() - 1 || z == 0 || z == size.getZ() - 1) {
					for (int y = 0; y < size.getY(); y++) {
						BlockPos checkPos = pos.add(template.transformedBlockPos(settings, new BlockPos(x, y, z)));
						IBlockState checkState = world.getBlockState(checkPos);
						if (!checkState.getBlock().isAir(checkState, world, checkPos)) {
							if (!(y <= 0 && (checkState.getBlock() == Blocks.NETHERRACK || checkState.getBlock() == Blocks.QUARTZ_ORE || checkState.getBlock() == Blocks.MAGMA))) {
								return;
							}
						}
					}
				}
			}
		}

		template.addBlocksToWorld(world, pos, settings);

		Map<BlockPos, String> dataBlocks = template.getDataBlocks(pos, settings);
		for (Entry<BlockPos, String> entry : dataBlocks.entrySet()) {
			String[] tokens = entry.getValue().split(" ");
			if (tokens.length == 0)
				return;

			BlockPos dataPos = entry.getKey();

			if (tokens[0].equals("pedestal")) {
				IBlockState chestState = InfernumBlocks.PEDESTAL.getDefaultState();
				world.setBlockState(dataPos, chestState);
				TileEntity tile = world.getTileEntity(dataPos);
				if (tile instanceof TilePedestal) {
					((TilePedestal) tile).setStack(new ItemStack(InfernumItems.KNOWLEDGE_BOOK));
				}
			}
	
		}

	}

	private static int findPosY(World world, BlockPos pos) {
		BlockPos checkPos = new BlockPos(pos.getX(), 120, pos.getZ());

		int airBlocks = 0;
		for (int yOffset = 1; yOffset <= 88; yOffset++) {
			if (world.isAirBlock(checkPos.down(yOffset))) {
				airBlocks++;
			} else {
				if (airBlocks > 10) {
					return (120 - yOffset) + 1;
				}
				airBlocks = 0;
			}
		}
		return -1;
	}

}
