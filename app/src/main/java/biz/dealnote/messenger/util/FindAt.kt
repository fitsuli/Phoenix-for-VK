package biz.dealnote.messenger.util

import java.util.*

class FindAt {

    constructor() {
        this.q = null
        this.offset = 0
        this.ended = false
    }

    constructor(q: String) {
        this.q = q
        this.offset = 0
        this.ended = false
    }

    constructor(q: String, offset: Int) {
        this.q = q
        this.offset = offset
        this.ended = false
    }

    constructor(q: String, offset: Int, ended: Boolean) {
        this.q = q
        this.offset = offset
        this.ended = ended
    }

    fun do_compare(q: String?): Boolean {
        if (Utils.isEmpty(q) && Utils.isEmpty(this.q)) {
            return true
        } else if (!Utils.isEmpty(this.q) && !Utils.isEmpty(q)) {
            if (this.q!!.toLowerCase(Locale.ROOT) == q!!.toLowerCase(Locale.ROOT)) return true
        }
        this.q = q
        this.offset = 0
        this.ended = false
        return false
    }

    fun reset(): FindAt {
        this.offset = 0
        this.ended = false
        return this
    }

    fun isEnded(): Boolean {
        return ended
    }

    fun isSearchMode(): Boolean {
        return !Utils.isEmpty(q)
    }

    fun getOffset(): Int {
        return offset
    }

    fun getQuery(): String? {
        return q
    }

    private var q: String?
    private var offset: Int
    private var ended: Boolean
}