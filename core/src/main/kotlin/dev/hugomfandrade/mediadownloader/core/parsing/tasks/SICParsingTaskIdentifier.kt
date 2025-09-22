package dev.hugomfandrade.mediadownloader.core.parsing.tasks

class SICParsingTaskIdentifier : ParsingTaskDelegate(listOf(
        SICParsingTaskV4(),
        SICParsingTaskV3(),
        SICParsingTaskV2(),
        SICParsingTaskV1()))