package rocks.poopjournal.metadataremover

data class Version(
        val major: Int,
        val minor: Int = 0,
        val patch: Int = 0,
        val build: Int = 0, // bump for dogfood builds, public betas, etc.
        private val buildType: String? = null
) {
    init {
        check(major >= 0)
        check(minor in 0 until 100)
        check(patch in 0 until 100)
        check(build in 0 until 100)
    }

    val code = major * 1000000 + minor * 10000 + patch * 100 + build
    val name = toString(buildVisibility = Visibility.NON_ZERO)
    val shortName = toString(
            patchVisibility = Visibility.NON_ZERO,
            buildVisibility = Visibility.NON_ZERO
    )
    val fullName = toString(
            minorVisibility = Visibility.ALWAYS,
            patchVisibility = Visibility.ALWAYS,
            buildVisibility = Visibility.ALWAYS
    )

    fun toString(
            minorDelimiter: Char = '.',
            minorVisibility: Visibility = Visibility.ALWAYS,
            patchDelimiter: Char = '.',
            patchVisibility: Visibility = Visibility.ALWAYS,
            buildDelimiter: Char = '-',
            buildVisibility: Visibility = Visibility.NEVER
    ): String {
        val showMinor = minorVisibility == Visibility.ALWAYS ||
                (minorVisibility == Visibility.NON_ZERO && minor > 0)
        val showPatch = patchVisibility == Visibility.ALWAYS ||
                (patchVisibility == Visibility.NON_ZERO && patch > 0)
        val showBuild = buildVisibility == Visibility.ALWAYS ||
                (buildVisibility == Visibility.NON_ZERO && build > 0)

        return StringBuilder()
                .apply {
                    append(major)
                    if (showMinor) {
                        append(minorDelimiter)
                        append(minor)
                        if (showPatch) {
                            append(patchDelimiter)
                            append(patch)
                            if (showBuild) {
                                append(buildDelimiter)
                                if (buildType != null) append(buildType)
                                append(build)
                            }
                        }
                    }
                }
                .toString()
    }

    override fun toString() = name

    enum class Visibility {
        ALWAYS,
        NON_ZERO,
        NEVER
    }
}