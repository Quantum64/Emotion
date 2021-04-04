package co.q64.emotion.core.opcode

sealed interface OpcodeMarker {
    interface Entry : OpcodeMarker
    interface Exit : OpcodeMarker
}

sealed interface Iterate : OpcodeMarker.Entry {
    object Standard : Iterate
    object Push : Iterate
}

sealed interface Compare : OpcodeMarker {
    object True : Compare {
        override fun toString() = "true"
    }

    object False : Compare {
        override fun toString() = "false"
    }

    object Greater : Compare {
        override fun toString() = "greater"
    }

    object Less : Compare {
        override fun toString() = "less"
    }

    object GreaterEqual : Compare {
        override fun toString() = "greater-equal"
    }

    object LessEqual : Compare {
        override fun toString() = "less-equal"
    }

    object Equal : Compare {
        override fun toString() = "equal"
    }

    object NotEqual : Compare {
        override fun toString() = "not-equal"
    }
}

sealed interface Control : OpcodeMarker {
    object End : Control, OpcodeMarker.Exit
    object Else : Control
    object Break : Control
    object Continue : Control
    object Function : Control

    sealed interface Internal : Control {
        object End2 : Internal
        object End3 : Internal
    }
}

sealed interface Compress : OpcodeMarker {
    object Single : Compress
    object Pair : Compress
    object Shoco : Compress
    object Base : Compress
}