package com.babynutricare.core.domain.weaning

/**
 * 辅食规则
 * 定义不同月龄的辅食添加规范、饮食禁忌和过敏源
 */
object WeaningRules {

    /**
     * 辅食阶段定义
     */
    data class WeaningStage(
        val minMonth: Int,
        val maxMonth: Int,
        val stageName: String,
        val description: String,
        val dailyMeals: Int,
        val texture: String,
        val allowedFoods: List<String>,
        val forbiddenFoods: List<String>
    )

    val stages = listOf(
        WeaningStage(
            minMonth = 0,
            maxMonth = 6,
            stageName = "纯奶期",
            description = "纯母乳/配方奶喂养，不添加任何辅食",
            dailyMeals = 0,
            texture = "液体",
            allowedFoods = emptyList(),
            forbiddenFoods = listOf("所有固体食物", "水（少量）", "果汁", "蜂蜜")
        ),
        WeaningStage(
            minMonth = 6,
            maxMonth = 7,
            stageName = "初期辅食",
            description = "第一口辅食，首选强化铁米粉，每日1次",
            dailyMeals = 1,
            texture = "糊状",
            allowedFoods = listOf("强化铁米粉", "米糊", "胡萝卜泥", "土豆泥", "南瓜泥", "苹果泥", "香蕉泥"),
            forbiddenFoods = listOf("盐", "糖", "蜂蜜", "鸡蛋清", "牛奶", "大豆", "小麦", "坚果", "海鲜", "整颗水果")
        ),
        WeaningStage(
            minMonth = 7,
            maxMonth = 9,
            stageName = "中期辅食",
            description = "引入泥糊状食物，每日1-2次",
            dailyMeals = 2,
            texture = "泥糊状",
            allowedFoods = listOf("强化铁米粉", "蔬菜泥", "水果泥", "蛋黄", "肉泥", "鱼泥", "豆腐泥", "稠粥"),
            forbiddenFoods = listOf("盐", "糖", "蜂蜜", "蛋清", "牛奶", "坚果", "海鲜", "整颗水果")
        ),
        WeaningStage(
            minMonth = 9,
            maxMonth = 12,
            stageName = "后期辅食",
            description = "增加食物质地，每日3次",
            dailyMeals = 3,
            texture = "碎末状",
            allowedFoods = listOf("稠粥", "软饭", "碎肉", "碎菜", "手指食物", "全蛋", "豆制品"),
            forbiddenFoods = listOf("盐", "蜂蜜", "坚果", "整颗葡萄", "果冻", "硬糖")
        ),
        WeaningStage(
            minMonth = 12,
            maxMonth = 18,
            stageName = "幼儿期",
            description = "接近成人饮食，每日3次",
            dailyMeals = 3,
            texture = "小块状",
            allowedFoods = listOf("软饭", "面条", "馄饨", "蒸蛋", "鱼肉", "瘦肉", "蔬菜", "水果"),
            forbiddenFoods = listOf("蜂蜜", "整颗坚果", "果冻", "腌制食品", "油炸食品")
        ),
        WeaningStage(
            minMonth = 18,
            maxMonth = 36,
            stageName = "幼儿期",
            description = "接近成人饮食，每日3次，可少量调味",
            dailyMeals = 3,
            texture = "小块状",
            allowedFoods = listOf("米饭", "面条", "包子", "炒菜", "鱼肉", "鸡肉", "瘦肉", "蔬菜", "水果"),
            forbiddenFoods = listOf("整颗坚果", "果冻", "腌制食品", "含酒精食品", "含咖啡因饮料")
        )
    )

    /**
     * 获取指定月龄的辅食阶段
     */
    fun getStage(month: Int): WeaningStage {
        return stages.firstOrNull { month in it.minMonth until it.maxMonth }
            ?: stages.last()
    }

    /**
     * 检查食材是否适合当前月龄
     */
    fun isFoodAllowed(foodName: String, month: Int): Boolean {
        val stage = getStage(month)
        if (month < 6) return false
        return stage.allowedFoods.any { foodName.contains(it.removeSuffix("泥").removeSuffix("糊").removeSuffix("粥")) }
            || stage.allowedFoods.isEmpty()
    }

    /**
     * 获取月龄饮食禁忌
     */
    fun getForbiddenFoods(month: Int): List<String> {
        if (month < 6) return listOf("所有辅食")
        return getStage(month).forbiddenFoods
    }

    /**
     * 常见过敏源
     */
    val commonAllergens = listOf(
        "鸡蛋", "牛奶", "大豆", "小麦", "花生", "核桃",
        "杏仁", "腰果", "鱼", "虾", "蟹", "芒果", "菠萝", "猕猴桃"
    )
}

/**
 * 过敏源知识
 */
data class AllergySource(
    val name: String,
    val description: String,
    val symptoms: List<String>,
    val introductionAge: Int,       // 建议引入月龄
    val avoidanceStrategy: String
)

object AllergySourceRepository {
    val sources = listOf(
        AllergySource(
            name = "鸡蛋",
            description = "常见的蛋白质过敏源，蛋清比蛋黄更易致敏",
            symptoms = listOf("皮疹", "腹泻", "呕吐", "湿疹加重"),
            introductionAge = 8,
            avoidanceStrategy = "先尝试蛋黄，观察3-5天无过敏反应后再逐步添加蛋清"
        ),
        AllergySource(
            name = "牛奶",
            description = "常见的蛋白质过敏源，乳糖不耐受常见于亚洲婴幼儿",
            symptoms = listOf("腹泻", "腹胀", "湿疹", "呕吐"),
            introductionAge = 12,
            avoidanceStrategy = "1岁前不建议直接饮用鲜牛奶，可用配方奶替代"
        ),
        AllergySource(
            name = "花生",
            description = "高致敏坚果，过敏反应可能较严重",
            symptoms = listOf("皮疹", "呼吸困难", "呕吐", "过敏性休克"),
            introductionAge = 12,
            avoidanceStrategy = "1岁前避免接触，1岁后少量尝试并观察反应"
        ),
        AllergySource(
            name = "虾蟹",
            description = "高致敏海鲜类，建议较晚引入",
            symptoms = listOf("皮疹", "腹泻", "呕吐", "呼吸困难"),
            introductionAge = 18,
            avoidanceStrategy = "1岁半前避免食用，首次尝试时少量并观察"
        ),
        AllergySource(
            name = "芒果",
            description = "常见致敏水果，尤其是未成熟的芒果",
            symptoms = listOf("口唇红肿", "皮疹", "瘙痒"),
            introductionAge = 12,
            avoidanceStrategy = "1岁后少量尝试，避免直接接触果皮"
        ),
        AllergySource(
            name = "蜂蜜",
            description = "含肉毒杆菌芽孢，1岁以下婴儿可能引发肉毒杆菌中毒",
            symptoms = listOf("便秘", "嗜睡", "喂养困难", "肌张力降低"),
            introductionAge = 12,
            avoidanceStrategy = "1岁前严格禁止食用蜂蜜及含蜂蜜食品"
        )
    )

    fun getAllergen(name: String): AllergySource? {
        return sources.find { it.name == name }
    }
}