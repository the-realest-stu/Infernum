package infernum.common.items;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Multimap;

import infernum.Infernum;
import infernum.common.spells.InfernumSpells;
import infernum.common.spells.Spell;
import infernum.common.spells.SpellRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSpellBook extends ItemBase {

	public ItemSpellBook() {
		super("infernal_spell_book");
		setMaxStackSize(0);
	}

	public int getMetadata(int damage) {
		return damage;
	}

	@Override
	public boolean isDamageable() {
		return false;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return I18n.translateToLocal(getUnlocalizedName(stack)) + " - "
				+ I18n.translateToLocal(getCurrentSpell(stack).getUnlocalizedName() + ".name");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		if (!getCurrentSpell(stack).equals(Spell.EMPTY_SPELL)) {
			tooltip.add(I18n.translateToLocal(TextFormatting.DARK_RED + "" + TextFormatting.ITALIC
					+ getCurrentSpell(stack).getUnlocalizedName() + ".name"));
		}
	}

	public Spell getCurrentSpell(ItemStack stack) {
		return Spell.EMPTY_SPELL;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return getCurrentSpell(stack).getUseTime();
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		player.setActiveHand(hand);

		ItemStack stack = player.getHeldItem(hand);

		this.getCurrentSpell(stack).onCast(world, player, stack);
		return new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
		if (entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityLiving;
			this.getCurrentSpell(stack).onCastFinish(world, player, stack);
		}
		return stack;
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase entityLivingBase, int count) {
		if (entityLivingBase instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityLivingBase;
			this.getCurrentSpell(stack).onCastTick(player.getEntityWorld(), player, stack);
		}
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		return this.getCurrentSpell(stack).onCastMelee(player.getEntityWorld(), player, entity, stack);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		if (this.getCurrentSpell(stack).getRarity() >= 4) {
			return EnumRarity.EPIC;
		}
		if (this.getCurrentSpell(stack).getRarity() >= 3) {
			return EnumRarity.RARE;
		}
		if (this.getCurrentSpell(stack).getRarity() >= 3) {
			return EnumRarity.UNCOMMON;
		}
		return EnumRarity.COMMON;
	}

	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
		Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);
		if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
					new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", -1D, 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
					new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.0D, 0));
		}
		return multimap;
	}

}
