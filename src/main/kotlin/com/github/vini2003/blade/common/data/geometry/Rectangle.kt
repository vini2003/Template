package com.github.vini2003.blade.common.data.geometry

import com.github.vini2003.blade.common.data.Position
import com.github.vini2003.blade.common.data.PositionHolder
import com.github.vini2003.blade.common.data.Size
import com.github.vini2003.blade.common.data.SizeHolder

class Rectangle(val position: PositionHolder, val size: SizeHolder) {
	companion object {
		@JvmStatic
		fun empty(): Rectangle {
			return Rectangle(Position.of(0F, 0F), Size.of(0F, 0F))
		}
	}

	fun isWithin(x: Float, y: Float): Boolean {
		return x > position.x && x < position.x + size.width && y > position.y && y < position.y + size.height
	}
}