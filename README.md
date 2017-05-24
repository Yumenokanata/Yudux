# Android架构的一种尝试

结合Redux和Haskell对并发的处理，主要由LifeCollection（生命周期容器）和Redux核心组成。

整个架构保持单一状态（POJO），在MVP架构的基础上将View层划分为Layout、Repo、Handler和State，保证单项数据流