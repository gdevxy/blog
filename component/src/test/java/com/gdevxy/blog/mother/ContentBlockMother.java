package com.gdevxy.blog.mother;

import java.util.List;
import java.util.Set;

import com.gdevxy.blog.model.BlogPost;
import com.gdevxy.blog.model.contentful.Node;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ContentBlockMother {

	public BlogPost.ContentBlock.ContentBlockBuilder h1() {

		return BlogPost.ContentBlock.builder()
			.blocks(List.of())
			.value("Title")
			.node(Node.HEADING_1)
			.marks(Set.of());
	}

}
