package dev.vini2003.blade.common.widget.base

import dev.vini2003.blade.BL
import dev.vini2003.blade.client.data.PartitionedTexture
import dev.vini2003.blade.client.utilities.Instances
import dev.vini2003.blade.common.collection.base.ExtendedWidgetCollection
import dev.vini2003.blade.common.collection.base.WidgetCollection
import dev.vini2003.blade.common.handler.BaseScreenHandler
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.inventory.Inventory
import net.minecraft.screen.slot.Slot
import kotlin.properties.Delegates

open class SlotWidget(
	var slot: Int,
	var inventory: Inventory,
	var slotProvider: (Inventory, Int, Int, Int) -> Slot
) : AbstractWidget() {
	constructor(slot: Int, inventory: Inventory) : this(slot, inventory, { inv, id, x, y -> Slot(inv, id, x, y) })
	
	var backendSlot: Slot? = null

	var texture = PartitionedTexture(
		BL.id("textures/widget/slot.png"),
		18F,
		18F,
		0.05555555555555555556F,
		0.05555555555555555556F,
		0.05555555555555555556F,
		0.05555555555555555556F
	)

	override var hidden: Boolean by Delegates.observable(false) { _, _, _ ->
		updateSlotPosition()
	}

	fun updateSlotPosition() {
		if (hidden) {
			backendSlot?.x = Int.MAX_VALUE / 2
			backendSlot?.y = Int.MAX_VALUE / 2
		} else {
			backendSlot?.x = slotX
			backendSlot?.y = slotY
		}

		if (extended?.client == true) {
			updateSlotPositionDelegate()
		}
	}

	@Environment(EnvType.CLIENT)
	fun updateSlotPositionDelegate() {
		val screen = Instances.client().currentScreen as? HandledScreen<*> ?: return

		backendSlot?.x = backendSlot?.x?.minus(screen.x)
		backendSlot?.y = backendSlot?.y?.minus(screen.y)
	}

	private val slotX: Int
		get() = (x + (if (size.width <= 18) 1F else size.width / 2F - 9F)).toInt()

	private val slotY: Int
		get() = (y + (if (size.height <= 18) 1F else size.height / 2F - 9F)).toInt()

	override fun onAdded(extended: ExtendedWidgetCollection, immediate: WidgetCollection) {
		super.onAdded(extended, immediate)
		backendSlot = slotProvider(inventory, slot, slotX, slotY)
		backendSlot!!.index = slot

		if (extended is BaseScreenHandler) {
			extended.addSlot(backendSlot)
		}
	}

	override fun onRemoved(extended: ExtendedWidgetCollection, immediate: WidgetCollection) {
		super.onRemoved(extended, immediate)

		if (extended is BaseScreenHandler) {
			extended.removeSlot(backendSlot)
		}
	}

	override fun onLayoutChanged() {
		super.onLayoutChanged()
		updateSlotPosition()
	}

	override fun drawWidget(matrices: MatrixStack, provider: VertexConsumerProvider) {
		if (hidden) return

		texture.draw(matrices, provider, position.x, position.y, size.width, size.height)

		super.drawWidget(matrices, provider)
	}
}