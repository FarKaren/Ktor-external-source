package example.com.dtos.extensions

import example.com.dtos.responses.AnnotationDto
import example.com.model.Annotation


fun Annotation.toAnnotationDto(): AnnotationDto {
    return AnnotationDto(rac, lap, rap, refkey, vcfId, clnsig, clnrevstat, clnvc)
}