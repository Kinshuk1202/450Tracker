package com.kinshuk.a450tracker

class PS {
    var name:String? = null
    var iChecked:Boolean?=null
    var link:String?=null
    constructor(name: String,x:Boolean,link:String)
    {
        this.iChecked = x
        this.link = link
        this.name = name
    }
}